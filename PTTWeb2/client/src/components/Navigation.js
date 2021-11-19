import { useHistory } from "react-router-dom";
import { Toolbar, Typography, CssBaseline } from "@material-ui/core";
import { Link } from "@mui/material";
import Pomodoro from "../assets/pomodoro.png";

const linkStyle = {
  fontSize: "20px",
  margin: "7px",
  color: "#E0362A",
  textDecoration: "none",
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
      {/* <AppBar style={{ backgroundColor: "transparent" }} position="relative"> */}
        <Toolbar style={{paddingTop: 20}}>
          <img src={Pomodoro} width="70" alt="Pomodoro" />
          <Typography variant="h3">
            <div className="font-pan" style={{color: "#E0362A", paddingLeft: 10}}>
            Tomato Pomodoro
            </div>
          </Typography>
          <div style={{ marginLeft: "auto" }}>
            {isLogin ? (
              <Link
                id="log-out"
                style={linkStyle}
                component="button"
                variant="subtitle1"
                onClick={handleClick}
              >
                <div className="font-pan">
                Logout
                </div>
              </Link>
            ) : (
              <></>
            )}
          </div>
        </Toolbar>
      {/* </AppBar> */}
    </div>
  );
}

export default Navigation;
