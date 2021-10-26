import { Grid, Paper, Avatar, TextField, Button } from "@mui/material";
import LockOutlinedIcon from "@mui/icons-material/LockOutlined";
import { useHistory } from "react-router-dom";

function Login({ username, handleChange, users }) {
  let history = useHistory();
  const handleSubmit = () => {
    const userObject = users.find((element) => element.email === username);
    if (username === "admin") {
      history.push("/admin");
    } else {
      if (userObject) {
        history.push({ pathname: "/user", state: userObject });
      } else {
        alert("User not found");
      }
    }
  };
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
        <form onSubmit={handleSubmit}>
          <TextField
            label="Username"
            placeholder="Enter username"
            variant="standard"
            fullWidth
            required
            value={username}
            onChange={handleChange}
          />
          <Button type="submit" color="primary" fullWidth variant="contained">
            Sign in
          </Button>
        </form>
      </Paper>
    </Grid>
  );
}

export default Login;
