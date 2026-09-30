import {useColorScheme} from "@mui/material/styles"
import DarkModeTwoToneIcon from '@mui/icons-material/DarkModeTwoTone';
import LightModeTwoToneIcon from '@mui/icons-material/LightModeTwoTone';
import {IconButton, Tooltip} from "@mui/material";

export default function ThemeToggle() {
    const {mode, setMode} = useColorScheme()

    const nextMode = mode === "dark" ? "light" : "dark"

    return (
        <Tooltip title={`Toggle ${nextMode} mode`} arrow>
            <IconButton
                aria-label={`Switch to ${nextMode} mode`}
                onClick={() => setMode(nextMode)}
            >
                {mode === "dark" ? <DarkModeTwoToneIcon/> : <LightModeTwoToneIcon/>}
            </IconButton>
        </Tooltip>
    )
}
