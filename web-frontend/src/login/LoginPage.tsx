import {AppBar, Box, Button, Divider, Paper, Stack, TextField, Toolbar, Typography} from "@mui/material";
import ThemeToggle from "../component/ThemeToggle.tsx";
import {useState} from "react";
import type {FormEvent} from "react";

const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

export default function LoginPage() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [emailError, setEmailError] = useState("");
    const [passwordError, setPasswordError] = useState("");

    const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
        event.preventDefault();

        const trimmedEmail = email.trim();
        const nextEmailError = !trimmedEmail
            ? "Email is required."
            : !emailRegex.test(trimmedEmail)
                ? "Enter a valid email address."
                : "";
        const nextPasswordError = password ? "" : "Password is required.";

        setEmailError(nextEmailError);
        setPasswordError(nextPasswordError);

        if (nextEmailError || nextPasswordError) {
            return;
        }

        const credentials = {email: trimmedEmail, password: password};

        void fetch("/", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(credentials),
        });
    };

    return (
        <Box sx={{
            minHeight: "100vh",
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
            bgcolor: "background.default",
            px: 2,
        }}>
            <AppBar
                position="absolute"
                color="transparent"
                elevation={0}
                sx={{
                    bgcolor: "transparent",
                    boxShadow: "none",
                }}
            >
                <Toolbar sx={{justifyContent: "flex-end"}}>
                    <ThemeToggle/>
                </Toolbar>
            </AppBar>
            <Paper
                elevation={4}
                sx={{
                    width: "100%",
                    maxWidth: 420,
                    p: 4,
                }}>
                <Stack
                    component="form"
                    onSubmit={handleSubmit}
                    noValidate
                    spacing={2}
                >
                    <Typography
                        variant="h3"
                        component="h3"
                        sx={{
                            textAlign: "center",
                        }}
                    >
                        <>Sign in</>
                    </Typography>
                    <TextField
                        label="Email"
                        name="email"
                        type="email"
                        required
                        fullWidth
                        autoComplete="email"
                        value={email}
                        error={Boolean(emailError)}
                        helperText={emailError}
                        onChange={(event) => {
                            setEmail(event.target.value);
                            if (emailError) {
                                setEmailError("");
                            }
                        }}
                        slotProps={{
                            htmlInput: {
                                pattern: emailRegex.source,
                            },
                        }}
                    />
                    <TextField
                        label="Password"
                        name="password"
                        type="password"
                        required
                        fullWidth
                        autoComplete="current-password"
                        value={password}
                        error={Boolean(passwordError)}
                        helperText={passwordError}
                        onChange={(event) => {
                            setPassword(event.target.value);
                            if (passwordError) {
                                setPasswordError("");
                            }
                        }}
                    />
                    <Button type="submit" variant="contained">
                        Sign in
                    </Button>
                    <Divider>
                        or
                    </Divider>
                    <Button>
                        Forgot password?
                    </Button>
                </Stack>
            </Paper>
        </Box>
    )
}
