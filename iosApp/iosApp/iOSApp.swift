import SwiftUI
import shared // Bizim Kotlin modülümüz

@main
struct iOSApp: App {
    
    // Uygulama hafızaya yüklenirken bir kere çalışır
    init() {
        // Kotlin tarafında yazdığımız 'doInitKoin' fonksiyonunu çağırıyoruz.
        // Böylece yapay zeka, veritabanı vb. servisler hazırlanıyor.
        MainViewControllerKt.doInitKoin()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}