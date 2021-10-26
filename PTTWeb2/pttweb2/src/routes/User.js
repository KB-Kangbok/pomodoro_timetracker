import { useEffect, useState } from "react";
import axios from "axios";
import { Button, TextField, Grid } from "@material-ui/core";
import OutlinedInput from "@mui/material/OutlinedInput";
import InputLabel from "@mui/material/InputLabel";
import MenuItem from "@mui/material/MenuItem";
import FormControl from "@mui/material/FormControl";
import Select from "@mui/material/Select";

export default function User({
  location: {
    state: { firstName, lastName, id },
  },
}) {
  const [projects, setProjects] = useState([{ id: 0 }]);
  const [selectedProject, setSelectedProject] = useState({});
  const [input, setInput] = useState("");
  const [update, setUpdate] = useState(false);

  useEffect(() => {
    const getProjects = async () => {
      const { data } = await axios.get(
        `http://localhost:8080/users/${id}/projects`
      );
      const projects = [
        { id: 1, projectname: "Oliver Hansen" },
        { id: 2, projectname: "Van Henry" },
      ];
      setProjects(data);
      setUpdate(false);
    };
    getProjects();
  }, [update]);

  const handleSelect = (event) => {
    const {
      target: { value },
    } = event;
    console.log(value);
    setSelectedProject(value);
  };

  const handleDelete = async () => {
    // This is for test-case. Later delete console.log and uncomment axios part
    console.log(selectedProject.id);
    await axios.delete(
      `http://localhost:8080/users/${id}/projects/${selectedProject.id}`
    );
    setSelectedProject({});
    setUpdate(true);
  };

  const handleInput = (event) => {
    const {
      target: { value },
    } = event;
    setInput(value);
  };

  const handleCreate = async () => {
    await axios.post(`http://localhost:8080/users/${id}/projects`, {
      projectname: input,
    });
    setInput("");
    setUpdate(true);
  };

  const gridStyle = {
    padding: 20,
    height: "60vh",
    width: 500,
    margin: "20px auto",
  };

  return (
    <div style={{ margin: 20 }}>
      <h1>Manage Projects</h1>
      <Grid style={gridStyle}>
        <FormControl sx={{ m: 1, width: 200 }}>
          <InputLabel>Project</InputLabel>
          <Select
            value={selectedProject}
            onChange={handleSelect}
            input={<OutlinedInput label="Name" />}
          >
            {projects.map((project) => (
              <MenuItem key={project.id} value={project}>
                {project.projectname}
              </MenuItem>
            ))}
          </Select>
          <Button variant="contained" onClick={handleDelete}>
            Delete
          </Button>
        </FormControl>

        <FormControl>
          <TextField
            id="outlined-basic"
            label="New Project"
            variant="outlined"
            onChange={handleInput}
            value={input}
          />
          <Button variant="contained" onClick={handleCreate}>
            Create
          </Button>
        </FormControl>
      </Grid>
    </div>
  );
}
