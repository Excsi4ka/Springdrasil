import {useAuth} from "./auth/AuthContext.ts";
import {Navigate, Route, Routes} from "react-router-dom";
import HomePage from "./home/HomePage.tsx";
import LoginPage from "./login/LoginPage.tsx";
import AdminPage from "./admin/AdminPage.tsx";

export default function App() {
    const {loggedIn} = useAuth()

    return (
        <Routes>
            <Route path="/" element={<HomePage/>}/>
            <Route path="/login" element={<LoginPage/>}/>
            <Route path="/register" element={<LoginPage/>}/>
            <Route path="/admin" element={loggedIn ? <AdminPage/> : <Navigate to={"/login"}/>}/>
        </Routes>
    )
}

