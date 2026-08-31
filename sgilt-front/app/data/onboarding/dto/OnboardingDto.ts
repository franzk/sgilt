export interface VerifyTokenResponseDto {
  email: string
  setPasswordToken: string
}

export type OnboardingFlow = 'CLIENT' | 'PRESTATAIRE'

export interface TokenExpiredResponseDto {
  flow: OnboardingFlow | null
}

export interface ConfirmAccountRequestDto {
  setPasswordToken: string
  password: string
  acceptedTerms: boolean
}

export interface ConfirmAccountResponseDto {
  loginUrl: string
}
