import { useEffect, useMemo, useState } from 'react'
import api from '../services/api'
import { Box, Button, Chip, Container, Grid, Paper, Stack, TextField, Typography } from '@mui/material'
import { Bar, BarChart, Tooltip, XAxis, YAxis, ResponsiveContainer, Pie, PieChart, Cell } from 'recharts'

interface MatchResult {
  resumeId: number
  candidateName: string
  score: number
  matchedSkills: string[]
  explanation: string
  yearsOfExperience?: number
  uploadDate: string
}

export default function JobMatching() {
  const [description, setDescription] = useState('')
  const [title, setTitle] = useState('')
  const [requiredSkills, setRequiredSkills] = useState('java, spring, react')
  const [results, setResults] = useState<MatchResult[]>([])
  const [selected, setSelected] = useState<MatchResult | null>(null)

  const submit = async () => {
    const req = { title, description, requiredSkills: requiredSkills.split(',').map(s => s.trim()) }
    const res = await api.post('/match', req)
    setResults(res.data)
    setSelected(res.data[0])
  }

  const scoreData = useMemo(() => results.map(r => ({ name: r.candidateName, score: r.score })), [results])
  const skillCounts = useMemo(() => {
    const map = new Map<string, number>()
    results.forEach(r => r.matchedSkills?.forEach(s => map.set(s, (map.get(s) || 0) + 1)))
    return Array.from(map.entries()).map(([name, value]) => ({ name, value }))
  }, [results])

  const colors = ['#8884d8', '#82ca9d', '#ffc658', '#ff7f50', '#8dd1e1']

  return (
    <Container sx={{ mt: 4 }}>
      <Grid container spacing={2}>
        <Grid item xs={12} md={5}>
          <Paper sx={{ p: 2, mb: 2 }}>
            <Stack spacing={2}>
              <Typography variant="h6">Job Description</Typography>
              <TextField label="Title" value={title} onChange={e => setTitle(e.target.value)} />
              <TextField label="Description" value={description} onChange={e => setDescription(e.target.value)} multiline minRows={6} />
              <TextField label="Required skills (comma separated)" value={requiredSkills} onChange={e => setRequiredSkills(e.target.value)} />
              <Button variant="contained" onClick={submit}>Find matching candidates</Button>
            </Stack>
          </Paper>
          <Paper sx={{ p: 2 }}>
            <Typography variant="h6">Score distribution</Typography>
            <ResponsiveContainer width="100%" height={200}>
              <BarChart data={scoreData}>
                <XAxis dataKey="name" hide />
                <YAxis />
                <Tooltip />
                <Bar dataKey="score" fill="#1976d2" />
              </BarChart>
            </ResponsiveContainer>
          </Paper>
        </Grid>
        <Grid item xs={12} md={7}>
          <Paper sx={{ p: 2, mb: 2 }}>
            <Typography variant="h6" gutterBottom>Matches</Typography>
            <Stack spacing={2}>
              {results.map(r => (
                <Paper key={r.resumeId} sx={{ p: 2, border: selected?.resumeId === r.resumeId ? '2px solid #1976d2' : '1px solid #eee' }} onClick={() => setSelected(r)}>
                  <Stack direction="row" justifyContent="space-between">
                    <Typography variant="subtitle1">{r.candidateName}</Typography>
                    <Chip label={`Score ${r.score}`} color="primary" />
                  </Stack>
                  <Stack direction="row" spacing={1} mt={1} flexWrap="wrap">
                    {r.matchedSkills?.map(s => <Chip key={s} label={s} size="small" />)}
                  </Stack>
                  <Typography variant="body2" mt={1}>{r.explanation}</Typography>
                </Paper>
              ))}
            </Stack>
          </Paper>
          {selected && (
            <Paper sx={{ p: 2 }}>
              <Typography variant="h6">AI Recruiter Assistant</Typography>
              <Typography variant="subtitle1">{selected.candidateName}</Typography>
              <Typography variant="body2" mt={1}>{selected.explanation}</Typography>
              <Typography variant="caption" display="block" mt={2}>Uploaded: {selected.uploadDate}</Typography>
            </Paper>
          )}
          <Paper sx={{ p: 2, mt: 2 }}>
            <Typography variant="h6">Top skills across candidates</Typography>
            <ResponsiveContainer width="100%" height={240}>
              <PieChart>
                <Pie data={skillCounts} dataKey="value" nameKey="name" outerRadius={80} label>
                  {skillCounts.map((entry, index) => <Cell key={entry.name} fill={colors[index % colors.length]} />)}
                </Pie>
                <Tooltip />
              </PieChart>
            </ResponsiveContainer>
          </Paper>
        </Grid>
      </Grid>
    </Container>
  )
}
