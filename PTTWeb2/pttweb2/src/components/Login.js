import { Grid, Paper, Avatar, TextField, Button } from "@mui/material";
import LockOutlinedIcon from "@mui/icons-material/LockOutlined";

function Login() {
  const paperStyle = {
    padding: 20,
    height: "60vh",
    width: 280,
    margin: "20px auto",
  };
  const avatarStyle = { backgroundColor: "#419fc7" };
  return (
    <Grid>
      <Paper elevation={10} style={paperStyle}>
        <Grid align="center">
          <Avatar style={avatarStyle}>
            <LockOutlinedIcon />
          </Avatar>
          <h2>Sign in</h2>
        </Grid>
        <TextField
          label="Username"
          placeholder="Enter username"
          variant="standard"
          fullWidth
          required
        />
        <Button type="submit" color="primary" fullWidth variant="contained">
          Sign in
        </Button>
      </Paper>
    </Grid>
  );
}

export default Login;
