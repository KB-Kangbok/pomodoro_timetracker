import axios from "axios";
import { useEffect, useState } from "react";
import Login from "../components/Login";
import { apiUrl } from "../config.json";

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
  const [users, setUsers] = useState([]);

  useEffect(() => {
    const getUsers = async () => {
      const { data } = await axios.get(`${apiUrl}/users`);
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
        setIsLogin={setIsLogin}
      />
    </div>
  );
}

export default Home;
