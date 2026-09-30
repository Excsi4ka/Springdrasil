import type { ReactNode } from 'react'
import CssBaseline from '@mui/material/CssBaseline'
import {createTheme, ThemeProvider as MuiThemeProvider} from '@mui/material/styles'

const theme = createTheme({
    colorSchemes: {
        dark: true,
    },
})

export default function GlobalThemeProvider({children}: {children: ReactNode}) {
    return (
        <MuiThemeProvider
            theme={theme}
            defaultMode="light"
            noSsr
        >
            <CssBaseline/>
            {children}
        </MuiThemeProvider>
    )
}
