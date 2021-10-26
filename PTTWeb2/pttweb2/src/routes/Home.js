import axios from "axios";
import { useEffect } from "react";
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

function Home() {
  const username = useInput("");
  const [users, setUsers] = useState([]);

  useEffect(() => {
    const getUsers = async () => {
      const { data } = await axios.get("http://localhost:8080/users");
      setUsers(data);
    };
    getUsers();
  }, []);
  return (
    <div>
      <Login
        username={username.value}
        handleChange={username.onChange}
        users={users}
      />
    </div>
  );
}

export default Home;
