export interface VerifyTokenResponseDto {
  email: string
  setPasswordToken: string
}

export interface ConfirmAccountRequestDto {
  setPasswordToken: string
  password: string
}

export interface ConfirmAccountResponseDto {
  accessToken: string
  refreshToken: string
}
