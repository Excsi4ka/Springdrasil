import { createContext, useContext } from 'react'

export type AuthContextValue = {
  loggedIn: boolean
}

export const unauthenticatedAuth: AuthContextValue = {
  loggedIn: false,
}

export const AuthContext = createContext<AuthContextValue>(
  unauthenticatedAuth,
)

export function useAuth() {
  return useContext(AuthContext)
}
