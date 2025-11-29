import { Navigate, Route, Routes } from 'react-router-dom'
import LoginPage from './pages/LoginPage'
import Dashboard from './pages/Dashboard'
import ResumeManagement from './pages/ResumeManagement'
import JobMatching from './pages/JobMatching'
import { useEffect, useState } from 'react'
import { createTheme, CssBaseline, ThemeProvider } from '@mui/material'

const theme = createTheme()

function App() {
  const [token, setToken] = useState<string | null>(localStorage.getItem('token'))

  useEffect(() => {
    if (token) {
      localStorage.setItem('token', token)
    }
  }, [token])

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <Routes>
        <Route path="/login" element={<LoginPage onLogin={setToken} />} />
        <Route path="/" element={token ? <Dashboard /> : <Navigate to="/login" />} />
        <Route path="/resumes" element={token ? <ResumeManagement /> : <Navigate to="/login" />} />
        <Route path="/matching" element={token ? <JobMatching /> : <Navigate to="/login" />} />
      </Routes>
    </ThemeProvider>
  )
}

export default App
