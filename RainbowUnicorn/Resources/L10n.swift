import Foundation
import GameCore

public enum L10n {

    private static var resourceBundle: Bundle {
        #if SWIFT_PACKAGE
        return Bundle.module
        #else
        return Bundle.main
        #endif
    }

    public static func string(_ key: String, language: String) -> String {
        let code = language.lowercased() == "vi" ? "vi" : "en"
        if let path = resourceBundle.path(forResource: code, ofType: "lproj"),
           let langBundle = Bundle(path: path) {
            return langBundle.localizedString(forKey: key, value: key, table: nil)
        }
        return NSLocalizedString(key, bundle: resourceBundle, comment: "")
    }

    public static func format(_ key: String, language: String, _ args: CVarArg...) -> String {
        let fmt = string(key, language: language)
        return String(format: fmt, locale: Locale(identifier: language == "vi" ? "vi_VN" : "en_US"), arguments: args)
    }

    public static func stationLabel(_ station: StationType, language: String) -> String {
        return string("station_\(station.rawValue)", language: language)
    }
}
