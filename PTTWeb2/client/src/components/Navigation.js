import { useHistory } from "react-router-dom";
import { Toolbar, Typography, CssBaseline } from "@material-ui/core";
import { Link } from "@mui/material";
import Pomodoro from "../assets/pomodoro.png";
import "./Navigation.css";

const linkStyle = {
  fontSize: "20px",
  margin: "7px",
  color: "#E0362A",
  textDecoration: "none",
};

function Navigation({ isLogin, setIsLogin, isTest, setIsTest }) {
  let history = useHistory();
  const toggleTest = () => {
    console.log(isTest);
    setIsTest(!isTest);
    console.log(isTest);
  };
  const handleClick = () => {
    setIsLogin(false);
    history.push("/");
  };
  return (
    <div>
      <CssBaseline />
      <Toolbar style={{ paddingTop: 20 }}>
        <img src={Pomodoro} width="70" alt="Pomodoro" />
        <Typography variant="h3">
          <div
            className="font-pan"
            style={{ color: "#E0362A", paddingLeft: 10 }}
          >
            {isTest ? "Test Mode" : "Tomato Pomodoro"}
          </div>
        </Typography>
        <div style={{ marginLeft: "auto" }}>
          <button id="toggle-test-btn" onClick={toggleTest}>
            test mode
          </button>
          {isLogin ? (
            <Link
              id="log-out"
              style={linkStyle}
              component="button"
              variant="subtitle1"
              onClick={handleClick}
            >
              <div className="font-pan">Logout</div>
            </Link>
          ) : (
            <></>
          )}
        </div>
      </Toolbar>
    </div>
  );
}

export default Navigation;
