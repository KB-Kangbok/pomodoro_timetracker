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
  const [projects, setProjects] = useState([]);
  const [selectedProject, setSelectedProject] = useState({});
  console.log(`${firstName} ${lastName} ${id}`);
  useEffect(() => {
    const getProjects = async () => {
      const { data } = await axios.get(
        `http://localhost:8080/users/${id}/projects`
      );
      // This is for test-case, later delete this whole const projects part and make setProjects(data)
      const projects = [
        { id: 1, projectname: "Oliver Hansen" },
        { id: 2, projectname: "Van Henry" },
      ];
      setProjects(projects);
    };
    getProjects();
  }, []);

  const handleChange = (event) => {
    const {
      target: { value },
    } = event;
    console.log(value);
    setSelectedProject(value);
  };

  const handleDelete = () => {
    // This is for test-case. Later delete console.log and uncomment axios part
    console.log(selectedProject.id);
    // await axios.delete(
    //   `http://localhost:8080/users/${id}/projects/${selectedProject.id}`
    // );
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
            value={projects}
            onChange={handleChange}
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
          />
          <Button variant="contained">Create</Button>
        </FormControl>
      </Grid>
    </div>
  );
}
