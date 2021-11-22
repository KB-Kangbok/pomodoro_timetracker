import { Grid, Paper, Avatar, TextField, Button } from "@mui/material";
import LockOutlinedIcon from "@mui/icons-material/LockOutlined";
import { useHistory } from "react-router-dom";
import axios from "axios";
import { apiUrl } from "../config.json";
import { useEffect, useState } from "react";

export default function Login({ username, handleChange, setIsLogin }) {
  let history = useHistory();
  const [users, setUsers] = useState([]);
  useEffect(() => {
    const getUsers = async () => {
      const { data } = await axios.get(`${apiUrl}/users`);
      setUsers(data);
    };
    getUsers();
  }, []);
  const handleSubmit = async () => {
    const userObject = users.find((element) => element.email === username);
    if (username === "admin") {
      history.push("/admin");
      setIsLogin(true);
    } else {
      if (userObject) {
        history.push({ pathname: "/user", state: userObject });
        setIsLogin(true);
      } else {
        alert("User not found");
      }
    }
  };
  const paperStyle = {
    marginTop: 50,
    padding: 20,
    height: "45vh",
    width: 280,
    margin: "20px auto",
  };
  const avatarStyle = { backgroundColor: "rgb(16 134 16)" };
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
            id="user-login-input"
            label="Username"
            placeholder="Enter username"
            variant="standard"
            fullWidth
            required
            value={username}
            onChange={handleChange}
          />
          <Button
            id="login-btn"
            type="submit"
            color="primary"
            fullWidth
            variant="contained"
          >
            Sign in
          </Button>
        </form>
      </Paper>
    </Grid>
  );
}
