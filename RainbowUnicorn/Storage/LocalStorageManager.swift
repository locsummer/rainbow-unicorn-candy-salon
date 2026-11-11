import Foundation
import UIKit
import Photos
import GameCore

public final class LocalStorageManager {

    public static let shared = LocalStorageManager()
    public static let maxGalleryPhotos = 50
    private let saveFileName = "save_game.json"
    private let backupFileName = "save_game.bak"
    private let galleryDirName = "candy_gallery"

    private var documentsDir: URL {
        FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)[0]
    }

    private var saveURL: URL {
        documentsDir.appendingPathComponent(saveFileName)
    }

    private var backupURL: URL {
        documentsDir.appendingPathComponent(backupFileName)
    }

    private var galleryDirURL: URL {
        let dir = documentsDir.appendingPathComponent(galleryDirName)
        try? FileManager.default.createDirectory(at: dir, withIntermediateDirectories: true)
        return dir
    }

    public func loadSaveData() -> SaveData {
        if FileManager.default.fileExists(atPath: saveURL.path) {
            do {
                let raw = try String(contentsOf: saveURL, encoding: .utf8)
                let parsed = try SaveSerializer.fromJSONStrict(raw)
                if !FileManager.default.fileExists(atPath: backupURL.path) {
                    try? raw.write(to: backupURL, atomically: true, encoding: .utf8)
                }
                return parsed
            } catch {
                // Corrupt primary file -> recover automatically from backup (RULE-091, RULE-092)
                if FileManager.default.fileExists(atPath: backupURL.path),
                   let backupStr = try? String(contentsOf: backupURL, encoding: .utf8),
                   let recovered = try? SaveSerializer.fromJSONStrict(backupStr) {
                    let normalized = SaveSerializer.toJSON(recovered)
                    try? normalized.write(to: saveURL, atomically: true, encoding: .utf8)
                    return recovered
                }
            }
        } else if FileManager.default.fileExists(atPath: backupURL.path),
                  let backupStr = try? String(contentsOf: backupURL, encoding: .utf8),
                  let recovered = try? SaveSerializer.fromJSONStrict(backupStr) {
            let normalized = SaveSerializer.toJSON(recovered)
            try? normalized.write(to: saveURL, atomically: true, encoding: .utf8)
            return recovered
        }
        return SaveData()
    }

    public func saveSaveData(_ data: SaveData) {
        let json = SaveSerializer.toJSON(data)
        do {
            if FileManager.default.fileExists(atPath: saveURL.path),
               let existing = try? String(contentsOf: saveURL, encoding: .utf8),
               (try? SaveSerializer.fromJSONStrict(existing)) != nil {
                if FileManager.default.fileExists(atPath: backupURL.path) {
                    try? FileManager.default.removeItem(at: backupURL)
                }
                try? FileManager.default.copyItem(at: saveURL, to: backupURL)
            } else if !FileManager.default.fileExists(atPath: backupURL.path) {
                try? json.write(to: backupURL, atomically: true, encoding: .utf8)
            }
            try json.write(to: saveURL, atomically: true, encoding: .utf8)
        } catch {}
    }

    public func getGalleryPhotoURLs() -> [URL] {
        let contents = (try? FileManager.default.contentsOfDirectory(at: galleryDirURL, includingPropertiesForKeys: [.contentModificationDateKey])) ?? []
        return contents
            .filter { $0.pathExtension.lowercased() == "png" }
            .sorted { u1, u2 in
                let d1 = (try? u1.resourceValues(forKeys: [.contentModificationDateKey]))?.contentModificationDate ?? Date.distantPast
                let d2 = (try? u2.resourceValues(forKeys: [.contentModificationDateKey]))?.contentModificationDate ?? Date.distantPast
                return d1 > d2
            }
    }

    public func canAddPhoto() -> Bool {
        return getGalleryPhotoURLs().count < Self.maxGalleryPhotos
    }

    public func savePhotoToInternalGallery(image: UIImage) -> URL? {
        guard canAddPhoto() else { return nil }
        let fileName = "photo_\(Int64(Date().timeIntervalSince1970 * 1000)).png"
        let fileURL = galleryDirURL.appendingPathComponent(fileName)
        guard let pngData = image.pngData() else { return nil }
        do {
            try pngData.write(to: fileURL, options: .atomic)
            return fileURL
        } catch {
            return nil
        }
    }

    public func deletePhoto(at url: URL) -> Bool {
        guard url.deletingLastPathComponent().path == galleryDirURL.path else { return false }
        do {
            try FileManager.default.removeItem(at: url)
            return true
        } catch {
            return false
        }
    }

    public func exportPhotoToPhotosAlbum(image: UIImage, completion: @escaping (Bool) -> Void) {
        // Add-only access (RULE-094)
        PHPhotoLibrary.requestAuthorization(for: .addOnly) { status in
            guard status == .authorized || status == .limited else {
                DispatchQueue.main.async { completion(false) }
                return
            }
            PHPhotoLibrary.shared().performChanges({
                PHAssetChangeRequest.creationRequestForAsset(from: image)
            }) { success, _ in
                DispatchQueue.main.async {
                    completion(success)
                }
            }
        }
    }
}
