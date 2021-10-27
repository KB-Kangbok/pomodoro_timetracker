import { Link } from "react-router-dom";
import { Toolbar, AppBar, Typography, CssBaseline } from "@material-ui/core";
import Pomodoro from "../assets/pomodoro.png";
import styled from "styled-components";

const StyledLink = styled(Link)`
  margin-left: 5px;
  color: red;
  text-decoration: none;
`;

function Navigation() {
  return (
    <div>
      <CssBaseline />
      <AppBar style={{ backgroundColor: "#034B03" }} position="relative">
        <Toolbar>
          <img src={Pomodoro} width="40" alt="Pomodoro" />
          <Typography variant="h6">
            <StyledLink to="/">Tomato Pomodoro</StyledLink>
          </Typography>
          <div style={{ marginLeft: "auto" }}>
            {/* two links below are only for test */}
            <StyledLink to="/admin">Admin</StyledLink>
            <StyledLink to="/user">User</StyledLink>
          </div>
        </Toolbar>
      </AppBar>
    </div>
  );
}

export default Navigation;
