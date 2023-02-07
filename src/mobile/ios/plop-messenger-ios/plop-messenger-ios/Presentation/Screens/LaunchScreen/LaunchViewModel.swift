import RxSwift
import RxCocoa
import Foundation

final class LaunchViewModel: ViewModelType {
  struct Input {
    let autoLoginTrigger: Driver<Void>
  }
  
  struct Output {
    let autoLoginResult: Driver<Void>
  }
  
  private let coordinator: SceneCoordinator
  private let usecase = AuthUseCase()
  
  init(coordinator: SceneCoordinator) {
    self.coordinator = coordinator
  }
  
  func transform(_ input: Input) -> Output {
    let autoLoginResult = input.autoLoginTrigger
      .flatMap({ [unowned self] _ in
        return self.usecase.mockAutoLogin()
          .map({ result in
            switch result {
            case .success(_):
              self.coordinator.toHome()
            case .failure(_):
              self.coordinator.toLogin()
            }
          })
          .asDriverOnErrorJustComplete()
      })
      
    
    return Output(autoLoginResult: autoLoginResult)
  }
}
