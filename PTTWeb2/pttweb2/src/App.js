import "./App.css";
import { HashRouter, Route } from "react-router-dom";
import Home from "./routes/Home";
import Admin from "./routes/Admin";
import User from "./routes/User";
import Navigation from "./components/Navigation";

function App() {
  return (
    <HashRouter>
      <Navigation />
      <Route path="/" exact={true} component={Home} />
      <Route path="/admin" exact={true} component={Admin} />
      <Route path="/user" exact={true} component={User} />
    </HashRouter>
  );
}

export default App;
