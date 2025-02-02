//
//  ShareViewController.swift
//  ShareExtension
//
//  Created by Buzzler Technologies on 02/02/25.
//

import UIKit
import Social
import MobileCoreServices

class ShareViewController: SLComposeServiceViewController {

    let DEEP_LINK_URL = "myappnavigation://post"

    override func viewDidLoad() {
        super.viewDidLoad()
        Timer.scheduledTimer(timeInterval: 0.2, target: self, selector: #selector(self.didSelectPost), userInfo: nil, repeats: false)
    }

    override func didSelectPost() {
        guard let extensionContext = self.extensionContext else { return }
        let attachments = extensionContext.inputItems.compactMap { $0 as? NSExtensionItem }.flatMap { $0.attachments ?? [] }
        
        let dispatchGroup = DispatchGroup()
        
        for attachment in attachments {
            if attachment.hasItemConformingToTypeIdentifier(kUTTypeImage as String) {
                dispatchGroup.enter()
                attachment.loadItem(forTypeIdentifier: kUTTypeImage as String, options: nil) { (data, error) in
                    
                    if let url = data as? URL, let imageData = try? Data(contentsOf: url) {
                        self.saveImageToAppGroup(imageData)
                    } else if let imageData = data as? Data {
                        self.saveImageToAppGroup(imageData)
                    } else if let image = data as? UIImage, let imageData = image.pngData() {
                        self.saveImageToAppGroup(imageData)
                    }

                    dispatchGroup.leave()
                }
            }
        }
        
        dispatchGroup.notify(queue: .main) {
            extensionContext.completeRequest(returningItems: [], completionHandler: nil)
        }
    }

    private func saveImageToAppGroup(_ imageData: Data) {
        if let sharedContainer = FileManager.default.containerURL(forSecurityApplicationGroupIdentifier: "group.com.gyan.AwesomeProject") {
            let imagePath = sharedContainer.appendingPathComponent("sharedImage.png")
            try? imageData.write(to: imagePath)
            self.openMainApp()
        }
    }

    private func openMainApp() {
      if let url = URL(string: DEEP_LINK_URL) {
          var responder: UIResponder? = self
          while responder != nil {
              if let application = responder as? UIApplication {
                  application.open(url, options: [:], completionHandler: nil)
                  break
              }
              responder = responder?.next
          }
      }
    }
}
