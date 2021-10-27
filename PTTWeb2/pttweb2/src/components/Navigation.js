import { Link } from "react-router-dom";
import { Toolbar, AppBar, Typography, CssBaseline } from "@material-ui/core";
import Pomodoro from "../assets/pomodoro.png";
import styled from "styled-components";

const StyledLink = styled(Link)`
  margin-left: 30px;
  color: white;
  text-decoration: none
`;

function Navigation() {
  return (
    <div>
      <CssBaseline/>
      <AppBar style={{ backgroundColor: "#034B03" }} position="relative">
        <Toolbar>
          <img src={Pomodoro} width="40" />
          <Typography variant="h6" style={{'margin-left': 5,color: "red"}}>
            Tomato Pomodoro
          </Typography>
          <div style={{marginLeft: 'auto'}}>
            <StyledLink to="/">Home</StyledLink>
            <StyledLink to="/admin">Admin</StyledLink>
            <StyledLink to="/user">User</StyledLink>
          </div>
        </Toolbar>
      </AppBar>
    </div>
  );
}

export default Navigation;
