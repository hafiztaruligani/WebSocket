import SwiftUI
import shared

@main
struct iOSApp: App {

    init () {
        KoinInit_iosKt.InitKoin()
    }

	var body: some Scene {
		WindowGroup {
			ContentView()
		}
	}
}