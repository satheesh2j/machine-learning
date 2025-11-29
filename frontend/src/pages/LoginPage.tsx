import { Button, Container, Paper, Stack, TextField, Typography } from '@mui/material'
import { useState } from 'react'
import api from '../services/api'
import { useNavigate } from 'react-router-dom'

interface Props {
  onLogin: (token: string) => void
}

export default function LoginPage({ onLogin }: Props) {
  const [username, setUsername] = useState('admin')
  const [password, setPassword] = useState('admin')
  const [error, setError] = useState('')
  const navigate = useNavigate()

  const submit = async () => {
    try {
      const res = await api.post('/auth/login', { username, password })
      onLogin(res.data.token)
      navigate('/')
    } catch (e) {
      setError('Invalid credentials')
    }
  }

  return (
    <Container maxWidth="sm" sx={{ mt: 12 }}>
      <Paper sx={{ p: 4 }}>
        <Stack spacing={2}>
          <Typography variant="h5">Login</Typography>
          <TextField label="Username" value={username} onChange={e => setUsername(e.target.value)} />
          <TextField label="Password" type="password" value={password} onChange={e => setPassword(e.target.value)} />
          {error && <Typography color="error">{error}</Typography>}
          <Button variant="contained" onClick={submit}>Sign in</Button>
        </Stack>
      </Paper>
    </Container>
  )
}
