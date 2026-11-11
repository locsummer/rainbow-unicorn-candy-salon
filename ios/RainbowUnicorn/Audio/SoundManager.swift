import Foundation
import AVFoundation

public final class SoundManager: ObservableObject {

    public var bgmEnabled: Bool = true {
        didSet {
            guard bgmEnabled != oldValue else { return }
            if !bgmEnabled {
                stopBgm()
            } else if !isPaused {
                startBgm()
            }
        }
    }

    public var sfxEnabled: Bool = true

    private let engine = AVAudioEngine()
    private let bgmNode = AVAudioPlayerNode()
    private let sfxNode = AVAudioPlayerNode()
    private var isEngineConfigured = false
    private var isPaused = false
    private var isBgmPlaying = false
    private let format: AVAudioFormat? = AVAudioFormat(standardFormatWithSampleRate: 22050, channels: 1)

    public init() {
        configureAudioSession()
        observeInterruptions()
    }

    deinit {
        NotificationCenter.default.removeObserver(self)
        stopBgm()
        engine.stop()
    }

    private func configureAudioSession() {
        #if os(iOS)
        let session = AVAudioSession.sharedInstance()
        try? session.setCategory(.ambient, mode: .default, options: [.mixWithOthers])
        try? session.setActive(true)
        #endif
    }

    private func observeInterruptions() {
        #if os(iOS)
        NotificationCenter.default.addObserver(
            self,
            selector: #selector(handleInterruption(_:)),
            name: AVAudioSession.interruptionNotification,
            object: nil
        )
        #endif
    }

    #if os(iOS)
    @objc private func handleInterruption(_ notification: Notification) {
        guard let info = notification.userInfo,
              let typeValue = info[AVAudioSessionInterruptionTypeKey] as? UInt,
              let type = AVAudioSession.InterruptionType(rawValue: typeValue) else {
            return
        }
        if type == .began {
            onPause()
        } else if type == .ended {
            let optVal = info[AVAudioSessionInterruptionOptionKey] as? UInt ?? 0
            let options = AVAudioSession.InterruptionOptions(rawValue: optVal)
            if options.contains(.shouldResume) {
                onResume()
            }
        }
    }
    #endif

    private func ensureEngineRunning() {
        guard let format = format else { return }
        if !isEngineConfigured {
            engine.attach(bgmNode)
            engine.attach(sfxNode)
            engine.connect(bgmNode, to: engine.mainMixerNode, format: format)
            engine.connect(sfxNode, to: engine.mainMixerNode, format: format)
            isEngineConfigured = true
        }
        if !engine.isRunning {
            try? engine.start()
        }
    }

    public func startBgm() {
        guard bgmEnabled, !isBgmPlaying, let format = format else { return }
        ensureEngineRunning()
        isBgmPlaying = true

        let melody: [Double] = [261.63, 329.63, 392.00, 523.25]
        let sampleRate = format.sampleRate
        let frames = AVAudioFrameCount(sampleRate * 4.0)
        guard let buffer = AVAudioPCMBuffer(pcmFormat: format, frameCapacity: frames) else { return }
        buffer.frameLength = frames

        if let data = buffer.floatChannelData?[0] {
            for i in 0..<Int(frames) {
                let t = Double(i) / sampleRate
                let freq = melody[Int(t * 2.0) % melody.count]
                data[i] = Float(sin(2.0 * .pi * freq * t) * 0.12)
            }
        }
        bgmNode.scheduleBuffer(buffer, at: nil, options: .loops, completionHandler: nil)
        bgmNode.play()
    }

    public func stopBgm() {
        isBgmPlaying = false
        bgmNode.stop()
    }

    public func onPause() {
        isPaused = true
        stopBgm()
    }

    public func onResume() {
        isPaused = false
        configureAudioSession()
        if bgmEnabled {
            startBgm()
        }
    }

    public func playClick() {
        guard sfxEnabled else { return }
        playTone(frequency: 523.25, durationSec: 0.06, amplitude: 0.25)
    }

    public func playBubble() {
        guard sfxEnabled else { return }
        playTone(frequency: 440.0, durationSec: 0.08, amplitude: 0.25)
    }

    public func playSparkle() {
        guard sfxEnabled else { return }
        playSequence(frequencies: [659.25, 783.99, 1046.50], noteSec: 0.075, amplitude: 0.25)
    }

    public func playShutter() {
        guard sfxEnabled else { return }
        playSequence(frequencies: [1200.0, 600.0], noteSec: 0.05, amplitude: 0.30)
    }

    public func playVictory() {
        guard sfxEnabled else { return }
        playSequence(frequencies: [523.25, 659.25, 783.99, 1046.50], noteSec: 0.10, amplitude: 0.30)
    }

    private func playTone(frequency: Double, durationSec: Double, amplitude: Double) {
        playSequence(frequencies: [frequency], noteSec: durationSec, amplitude: amplitude)
    }

    private func playSequence(frequencies: [Double], noteSec: Double, amplitude: Double) {
        guard let format = format else { return }
        ensureEngineRunning()
        let sampleRate = format.sampleRate
        let framesPerNote = Int(sampleRate * noteSec)
        let totalFrames = AVAudioFrameCount(framesPerNote * frequencies.count)
        guard totalFrames > 0,
              let buffer = AVAudioPCMBuffer(pcmFormat: format, frameCapacity: totalFrames) else { return }
        buffer.frameLength = totalFrames

        if let data = buffer.floatChannelData?[0] {
            for (nIdx, freq) in frequencies.enumerated() {
                for j in 0..<framesPerNote {
                    let i = nIdx * framesPerNote + j
                    let t = Double(j) / sampleRate
                    let envelope = sin(.pi * (Double(j) / Double(max(1, framesPerNote))))
                    data[i] = Float(sin(2.0 * .pi * freq * t) * amplitude * envelope)
                }
            }
        }
        sfxNode.scheduleBuffer(buffer, at: nil, options: [], completionHandler: nil)
        if !sfxNode.isPlaying {
            sfxNode.play()
        }
    }
}
