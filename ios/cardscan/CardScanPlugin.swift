import Capacitor
import Foundation
import CardScan


@objc(CardScanPlugin)
class CardScanPlugin: CAPPlugin, ScanDelegate {

    @objc func show(_ call: CAPPluginCall) {
        DispatchQueue.main.async { [weak self] in
            guard let scanVC = ScanViewController.createViewController(withDelegate: self) else {
                call.reject(K.incompatibleMessage)
                        return
                    }
            
            UserDefaults().setValue(call.callbackId, forKey: K.callbackId)
            self?.bridge?.saveCall(call)
            
            self?.bridge?.presentVC(scanVC, animated: true, completion: nil)
        }
    }
    
    func userDidSkip(_ scanViewController: ScanViewController) {
        scanViewController.dismiss(animated: true)
        rejectedCall(K.cancelMessage)
    }

    func userDidCancel(_ scanViewController: ScanViewController) {
        scanViewController.dismiss(animated: true)
        rejectedCall(K.cancelMessage)
    }
    
    func userDidScanCard(
        _ scanViewController: ScanViewController,
        creditCard: CreditCard
    ) {
        let number = creditCard.number
        let expiryMonth = creditCard.expiryMonth ?? "??"
        let expiryYear = creditCard.expiryYear ?? "??"
        let name = creditCard.name ?? "??"
        
        let result = [
            K.number: number,
            K.expiryDate: "\(expiryMonth)/\(expiryYear)",
            K.name: name
        ]
        
        let id = UserDefaults().string(forKey: K.callbackId) ?? ""
        guard let call = self.bridge?.savedCall(withID: id) else {
            return
        }
        
        call.resolve(result)
        self.bridge?.releaseCall(call)
        
        scanViewController.dismiss(animated: true)
    }
    
    private func rejectedCall(_ message: String) {
        let id = UserDefaults().string(forKey: K.callbackId) ?? ""
        
        guard let call = self.bridge?.savedCall(withID: id) else {
            return
        }
        
        call.reject(message)
        self.bridge?.releaseCall(call)
    }
    
    private enum K {
        static let callbackId = "callbackId"
        
        static let number = "number"
        static let expiryDate = "expiryDate"
        static let name = "name"
        
        static let cancelMessage = "Scan was canceled"
        static let incompatibleMessage = "This device is incompatible with CardScan"
    }
}
