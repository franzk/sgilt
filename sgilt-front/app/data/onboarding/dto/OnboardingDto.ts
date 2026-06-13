export interface VerifyTokenResponseDto {
  email: string
  setPasswordToken: string
}

export interface ConfirmAccountRequestDto {
  setPasswordToken: string
  password: string
  acceptedTerms: boolean
}

export interface ConfirmAccountResponseDto {
  loginUrl: string
}
