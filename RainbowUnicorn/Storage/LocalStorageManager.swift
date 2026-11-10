import Foundation
import UIKit
import Photos
import GameCore

public final class LocalStorageManager {

    public static let shared = LocalStorageManager()
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
                let data = try Data(contentsOf: saveURL)
                let parsed = try JSONDecoder().decode(SaveData.self, from: data)
                return parsed
            } catch {
                // Primary file corrupted -> attempt recovery from backup (RULE-092)
                if FileManager.default.fileExists(atPath: backupURL.path),
                   let backupData = try? Data(contentsOf: backupURL),
                   let backupParsed = try? JSONDecoder().decode(SaveData.self, from: backupData) {
                    return backupParsed
                }
            }
        }
        return SaveData()
    }

    public func saveSaveData(_ data: SaveData) {
        do {
            let encoded = try JSONEncoder().encode(data)
            // Backup existing valid save file first
            if FileManager.default.fileExists(atPath: saveURL.path) {
                try? FileManager.default.copyItem(at: saveURL, to: backupURL)
            }
            try encoded.write(to: saveURL, options: .atomic)
        } catch {}
    }

    public func getGalleryPhotoURLs() -> [URL] {
        let contents = (try? FileManager.default.contentsOfDirectory(at: galleryDirURL, includingPropertiesForKeys: nil)) ?? []
        return contents.filter { $0.pathExtension.lowercased() == "png" }
            .sorted { (u1, u2) -> Bool in
                let d1 = (try? u1.resourceValues(forKeys: [.contentModificationDateKey]))?.contentModificationDate ?? Date.distantPast
                let d2 = (try? u2.resourceValues(forKeys: [.contentModificationDateKey]))?.contentModificationDate ?? Date.distantPast
                return d1 > d2
            }
    }

    public func canAddPhoto() -> Bool {
        return getGalleryPhotoURLs().count < 50
    }

    public func savePhotoToInternalGallery(image: UIImage) -> URL? {
        guard canAddPhoto() else { return nil }
        let fileName = "photo_\(Int64(Date().timeIntervalSince1970 * 1000)).png"
        let fileURL = galleryDirURL.appendingPathComponent(fileName)
        guard let pngData = image.pngData() else { return nil }
        do {
            try pngData.write(to: fileURL)
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
        PHPhotoLibrary.shared().performChanges({
            PHAssetChangeRequest.creationRequestForAsset(from: image)
        }) { success, _ in
            DispatchQueue.main.async {
                completion(success)
            }
        }
    }
}
