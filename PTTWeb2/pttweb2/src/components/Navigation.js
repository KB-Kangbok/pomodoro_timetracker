import { useHistory } from "react-router-dom";
import { Toolbar, AppBar, Typography, CssBaseline } from "@material-ui/core";
import { Link } from "@mui/material";
import Pomodoro from "../assets/pomodoro.png";

const linkStyle = {
  "margin-left": "5px",
  color: "red",
  "text-decoration": "none",
};

function Navigation({ isLogin, setIsLogin }) {
  let history = useHistory();
  const handleClick = () => {
    setIsLogin(false);
    history.push("/");
  };
  return (
    <div>
      <CssBaseline />
      <AppBar style={{ backgroundColor: "#034B03" }} position="relative">
        <Toolbar>
          <img src={Pomodoro} width="40" alt="Pomodoro" />
          <Typography variant="h6" color="secondary">
            Tomato Pomodoro
          </Typography>
          <div style={{ marginLeft: "auto" }}>
            {isLogin ? (
              <Link
                style={linkStyle}
                component="button"
                variant="subtitle1"
                onClick={handleClick}
              >
                Log Out
              </Link>
            ) : (
              <></>
            )}
          </div>
        </Toolbar>
      </AppBar>
    </div>
  );
}

export default Navigation;
