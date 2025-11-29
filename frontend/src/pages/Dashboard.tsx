import { Box, Card, CardContent, Grid, Typography, Button, Stack } from '@mui/material'
import { useEffect, useState } from 'react'
import api from '../services/api'
import { useNavigate } from 'react-router-dom'

export default function Dashboard() {
  const [resumeCount, setResumeCount] = useState(0)
  const navigate = useNavigate()

  useEffect(() => {
    api.get('/resumes').then(res => setResumeCount(res.data.length)).catch(() => setResumeCount(0))
  }, [])

  return (
    <Box p={4}>
      <Typography variant="h4" gutterBottom>Talent Acquisition Assistant</Typography>
      <Grid container spacing={2}>
        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Typography variant="h6">Resumes in knowledge base</Typography>
              <Typography variant="h3">{resumeCount}</Typography>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
      <Stack direction={{ xs: 'column', md: 'row' }} spacing={2} mt={4}>
        <Button variant="contained" onClick={() => navigate('/resumes')}>Upload Resumes</Button>
        <Button variant="outlined" onClick={() => navigate('/matching')}>Match Candidates to JD</Button>
      </Stack>
    </Box>
  )
}
