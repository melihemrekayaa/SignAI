import SwiftUI
import shared // Bizim Kotlin modülümüz

struct ContentView: UIViewControllerRepresentable {
    
    // Kotlin tarafındaki 'MainViewController' fonksiyonunu çağırıp
    // iOS'un anlayacağı bir View Controller'a dönüştürüyoruz.
    func makeUIViewController(context: Context) -> UIViewController {
        return MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
