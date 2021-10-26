import * as React from 'react';
import { Button, TextField, Grid } from '@material-ui/core';
import OutlinedInput from '@mui/material/OutlinedInput';
import InputLabel from '@mui/material/InputLabel';
import MenuItem from '@mui/material/MenuItem';
import FormControl from '@mui/material/FormControl';
import Select from '@mui/material/Select';


const projects = [
  'Oliver Hansen',
  'Van Henry',
];


export default function User() {
  const [project, setProject] = React.useState([]);

  const handleChange = (event) => {
    const {
      target: { value },
    } = event;
    setProject(
      // On autofill we get a the stringified value.
      typeof value === 'string' ? value.split(',') : value,
    );
  };

  const gridStyle = {
    padding: 20,
    height: "60vh",
    width: 500,
    margin: "20px auto",
  };

  return (
    <div style={{margin: 20}}>
      <h1>Manage Projects</h1>
      <Grid style={gridStyle}>
          <FormControl sx={{ m: 1, width: 200 }}>
            <InputLabel>Project</InputLabel>
            <Select
              value={project}
              onChange={handleChange}
              input={<OutlinedInput label="Name" />}
            >
              {projects.map((name) => (
                <MenuItem
                  key={name}
                  value={name}
                >
                  {name}
                </MenuItem>
              ))}
            </Select>
            <Button variant="contained">Delete</Button>  
          </FormControl>  

        <FormControl>
          <TextField id="outlined-basic" label="New Project" variant="outlined" />
          <Button variant="contained">Create</Button>
        </FormControl>
      </Grid>
    </div>
  );
}