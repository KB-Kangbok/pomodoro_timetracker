import { useState } from "react";
import Login from "../components/Login";

const useInput = (initialValue) => {
  const [value, setValue] = useState(initialValue);
  const onChange = (event) => {
    const {
      target: { value },
    } = event;
    setValue(value);
  };
  return { value, onChange };
};

function Home({ setIsLogin }) {
  const username = useInput("");

  return (
    <div>
      <Login
        username={username.value}
        handleChange={username.onChange}
        setIsLogin={setIsLogin}
      />
    </div>
  );
}

export default Home;
