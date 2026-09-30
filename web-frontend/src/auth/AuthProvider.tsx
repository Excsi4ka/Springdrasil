import type { PropsWithChildren } from 'react'
import { AuthContext, unauthenticatedAuth } from './AuthContext'

export default function AuthProvider({ children }: PropsWithChildren) {
  return (
      <AuthContext.Provider value={unauthenticatedAuth}>
        {children}
      </AuthContext.Provider>
  )
}
