import { useEffect, useState } from 'react'
import api from '../services/api'
import { Box, Button, Chip, Container, Paper, Stack, Table, TableBody, TableCell, TableHead, TableRow, TextField, Typography } from '@mui/material'

interface Resume {
  id: number
  candidateName: string
  fileName: string
  uploadedAt: string
  active: boolean
  yearsOfExperience?: number
  skills: string[]
  summary: string
}

export default function ResumeManagement() {
  const [resumes, setResumes] = useState<Resume[]>([])
  const [file, setFile] = useState<File | null>(null)
  const [candidateName, setCandidateName] = useState('')

  const load = () => api.get('/resumes').then(res => setResumes(res.data))

  useEffect(() => { load() }, [])

  const upload = async () => {
    if (!file) return
    const form = new FormData()
    form.append('file', file)
    form.append('candidateName', candidateName)
    await api.post('/resumes/upload', form, { headers: { 'Content-Type': 'multipart/form-data' } })
    setCandidateName('')
    setFile(null)
    load()
  }

  const deactivate = async (id: number) => {
    await api.post(`/resumes/${id}/deactivate`)
    load()
  }

  return (
    <Container sx={{ mt: 4 }}>
      <Typography variant="h5" gutterBottom>Resume Management</Typography>
      <Paper sx={{ p: 2, mb: 3 }}>
        <Stack direction={{ xs: 'column', md: 'row' }} spacing={2} alignItems="center">
          <Button variant="contained" component="label">
            Choose file
            <input type="file" hidden onChange={e => setFile(e.target.files?.[0] || null)} />
          </Button>
          <TextField label="Candidate name" value={candidateName} onChange={e => setCandidateName(e.target.value)} />
          <Button variant="outlined" onClick={upload}>Upload</Button>
          {file && <Typography>{file.name}</Typography>}
        </Stack>
      </Paper>
      <Paper>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Name</TableCell>
              <TableCell>File</TableCell>
              <TableCell>Uploaded</TableCell>
              <TableCell>Skills</TableCell>
              <TableCell>Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {resumes.map(r => (
              <TableRow key={r.id}>
                <TableCell>{r.candidateName}</TableCell>
                <TableCell>{r.fileName}</TableCell>
                <TableCell>{r.uploadedAt}</TableCell>
                <TableCell>
                  <Stack direction="row" spacing={1}>
                    {r.skills?.map(s => <Chip key={s} label={s} />)}
                  </Stack>
                </TableCell>
                <TableCell>
                  <Button size="small" onClick={() => deactivate(r.id)}>Deactivate</Button>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </Paper>
    </Container>
  )
}
