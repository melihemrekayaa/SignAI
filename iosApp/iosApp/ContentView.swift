import SwiftUI
import shared   // KMP shared framework

struct ContentView: View {

    private let generator: SignatureGenerator =
        FakeSignatureGenerator(promptBuilder: SignaturePromptBuilder())

    @State private var fullName: String = ""
    @State private var nickname: String = ""
    @State private var occupation: String = ""
    @State private var isGenerating: Bool = false

    @State private var aiPrompt: String? = nil
    @State private var aiInstructions: String? = nil
    @State private var errorMessage: String? = nil

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(alignment: .leading, spacing: 16) {

                    Text("SignAI")
                        .font(.largeTitle)
                        .bold()

                    Text("Seni tanıyalım, sana özel imza tasarlayalım.")
                        .font(.subheadline)

                    TextField("Ad Soyad", text: $fullName)
                        .textFieldStyle(.roundedBorder)
                        .textInputAutocapitalization(.words)

                    TextField("Kısa ad / lakap (opsiyonel)", text: $nickname)
                        .textFieldStyle(.roundedBorder)

                    TextField("Meslek (opsiyonel)", text: $occupation)
                        .textFieldStyle(.roundedBorder)

                    if let errorMessage {
                        Text(errorMessage)
                            .font(.footnote)
                            .foregroundStyle(.red)
                    }

                    // 👇 Button söz dizimini explicit yazıyoruz
                    Button(
                        action: {
                            generateSignature()
                        },
                        label: {
                            Text(isGenerating ? "Üretiliyor..." : "İmza fikri üret")
                                .frame(maxWidth: .infinity)
                        }
                    )
                    .buttonStyle(.borderedProminent)
                    .disabled(isGenerating)

                    if let aiPrompt {
                        VStack(alignment: .leading, spacing: 8) {
                            Text("AI'ye gidecek prompt:")
                                .font(.headline)
                            Text(aiPrompt)
                                .font(.footnote)
                        }
                        .padding(.top, 16)
                    }

                    if let aiInstructions {
                        VStack(alignment: .leading, spacing: 8) {
                            Text("AI'nin imza tarifi (şimdilik demo):")
                                .font(.headline)
                            Text(aiInstructions)
                                .font(.footnote)
                        }
                        .padding(.top, 16)
                    }

                    Spacer(minLength: 32)
                }
                .padding(20)
            }
            .navigationBarHidden(true)
        }
    }

    private func generateSignature() {
        guard !fullName.trimmingCharacters(in: .whitespaces).isEmpty else {
            errorMessage = "Lütfen ad soyad gir."
            return
        }

        errorMessage = nil
        isGenerating = true
        aiPrompt = nil
        aiInstructions = nil

        let profile = SignatureProfile(
            fullName: fullName.trimmingCharacters(in: .whitespaces),
            nickname: nickname.trimmingCharacters(in: .whitespaces).isEmpty ? nil : nickname,
            occupation: occupation.trimmingCharacters(in: .whitespaces).isEmpty ? nil : occupation,
            dominantHand: "right",
            style: SignatureStyle.modern,
            trait: PersonalityTrait.creative
        )

        generator.generate(profile: profile) { result, error in
            DispatchQueue.main.async {
                self.isGenerating = false

                if let error {
                    self.errorMessage = "İmza üretiminde hata: \(error.localizedDescription)"
                    return
                }

                if let result {
                    self.aiPrompt = result.prompt
                    self.aiInstructions = result.instructions
                }
            }
        }
    }
}

#Preview {
    ContentView()
}
