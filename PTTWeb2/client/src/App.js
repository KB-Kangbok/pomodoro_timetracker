import "./App.css";
import { HashRouter, Route } from "react-router-dom";
import Home from "./routes/Home";
import Admin from "./routes/Admin";
import User from "./routes/User";
import Navigation from "./components/Navigation";
import { useState } from "react";

function App() {
  const [isLogin, setIsLogin] = useState(false);
  return (
    <HashRouter>
      <Navigation isLogin={isLogin} setIsLogin={setIsLogin} />
      <Route
        path="/"
        exact={true}
        render={(props) => <Home {...props} setIsLogin={setIsLogin} />}
      />
      <Route path="/admin" exact={true} component={Admin} />
      <Route path="/user" exact={true} component={User} />
    </HashRouter>
  );
}

export default App;