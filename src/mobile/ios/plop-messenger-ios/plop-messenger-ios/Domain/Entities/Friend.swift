import Foundation

struct Friend: Codable {
  enum Status: Codable {
    case online
    case offline
  }
  
  let uid: Int64
  let status: Status
  let block: Bool
  let imageURL: String?
  let name: String
}
