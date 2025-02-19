export interface Message {
  id: string
  dateTime: Date
  sender: string
  receiver: string
  message: string
  read: boolean
}
