import axios from "axios";
import { apiUrl } from "../config.json";
import {
  FormControl,
  InputLabel,
  OutlinedInput,
  MenuItem,
  Button,
  Select,
  Typography,
  Grid,
  FormControlLabel,
  Checkbox,
  TextField,
  FormGroup,
} from "@mui/material";
import { LocalizationProvider, DateTimePicker } from "@mui/lab";
import AdapterDateFns from "@mui/lab/AdapterDateFns";
import { useState } from "react";
import Result from "./Result";

const getTime = (time) => {
  const date = new Date(time);
  const year = date.getFullYear();
  const month = "0" + (date.getMonth() + 1);
  const day = "0" + date.getDate();
  // Hours part from the timestamp
  const hours = "0" + date.getHours();
  // Minutes part from the timestamp;
  const minutes = "0" + date.getMinutes();
  // Will display time in 2019-02-19T10:30Z format
  return (
    year +
    "-" +
    month.substr(-2) +
    "-" +
    day.substr(-2) +
    "T" +
    hours.substr(-2) +
    ":" +
    minutes.substr(-2) +
    "Z"
  );
};

export default function Report({ id, projects, dateAdapter }) {
  const currTime = Date.now();
  const [selectedProjectId, setSelectedProjectId] = useState("");
  const [startTime, setStartTime] = useState(currTime);
  const [startTimeString, setStartTimeString] = useState(getTime(currTime));
  const [endTime, setEndTime] = useState(currTime);
  const [endTimeString, setEndTimeString] = useState(getTime(currTime));
  const [totalHour, setTotalHour] = useState(false);
  const [totalPomodoro, setTotalPomodoro] = useState(false);
  const [reportReady, setReportReady] = useState(false);
  const [response, setResponse] = useState("");

  // console.log(value);
  const gridStyle = {
    padding: 20,
    height: "60vh",
    width: "100%",
    margin: "20 auto",
  };

  const reset = () => {
    const currTime = Date.now();
    setSelectedProjectId("");
    setStartTime(currTime);
    setEndTime(currTime);
    setStartTimeString(getTime(currTime));
    setEndTimeString(getTime(currTime));
    setTotalHour(false);
    setTotalPomodoro(false);
    setResponse("");
  };

  const handleSelect = (event) => {
    const {
      target: { value },
    } = event;
    setSelectedProjectId(value);
  };

  const handleStartTimeChange = (newValue) => {
    setStartTime(newValue);
    setStartTimeString(getTime(newValue));
  };
  const handleEndTimeChange = (newValue) => {
    setEndTime(newValue);
    setEndTimeString(getTime(newValue));
  };

  const handleChangeHour = (event) => {
    setTotalHour(event.target.checked);
  };

  const handleChangePomodoro = (event) => {
    setTotalPomodoro(event.target.checked);
  };

  const handleClick = async () => {
    const url = `${apiUrl}/users/${id}/projects/${selectedProjectId}/report`;
    const params = { from: startTimeString, to: endTimeString };
    if (totalHour) {
      params.includeTotalHoursWorkedOnProject = true;
    }
    if (totalPomodoro) {
      params.includeCompletedPomodoros = true;
    }
    console.log(params);
    try {
      const res = await axios.get(url, { params: params });
      console.log(res);
      if (res.status === 200) {
        setResponse(res.data);
        console.log(res.data);
        setReportReady(true);
        console.log(response);
      }
    } catch (e) {
      if (e.response.status === 400) {
        alert("Bad request");
      } else if (e.response.status === 404) {
        alert("User, project, or session not found");
      } else {
        alert(`Failed with ${e.response.status} status`);
      }
    }
  };

  return (
    <div style={{ margin: 30 }}>
      <Typography variant="inherit" component="h1" style={{ padding: 0 }}>
        Report
      </Typography>
      <Grid style={gridStyle}>
        <Grid
          container
          style={{ justifyContent: "center", alignContent: "center" }}
        >
          {!reportReady && (
            <FormGroup sx={{ width: 300 }}>
              <FormControl>
                <InputLabel variant="outlined">Projects</InputLabel>
                <Select
                  id="projects"
                  value={selectedProjectId}
                  onChange={handleSelect}
                  input={<OutlinedInput label="Name" />}
                >
                  {projects.map((project) => (
                    <MenuItem key={project.id} value={project.id}>
                      {project.projectname}
                    </MenuItem>
                  ))}
                </Select>
                <LocalizationProvider dateAdapter={AdapterDateFns}>
                  <FormControl sx={{ marginTop: 2 }}>
                    <DateTimePicker
                      id="start-time-picker"
                      label="Start Time Picker"
                      value={startTime}
                      onChange={handleStartTimeChange}
                      renderInput={(params) => <TextField {...params} />}
                    />
                  </FormControl>
                  <FormControl sx={{ marginTop: 2 }}>
                    <DateTimePicker
                      label="End Time Picker"
                      value={endTime}
                      onChange={handleEndTimeChange}
                      renderInput={(params) => <TextField {...params} />}
                    />
                  </FormControl>
                </LocalizationProvider>
                <FormControlLabel
                  control={
                    <Checkbox
                      id="time-checkbox"
                      checked={totalHour}
                      onChange={handleChangeHour}
                    />
                  }
                  label="Include Total Hours"
                />
                <FormControlLabel
                  control={
                    <Checkbox
                      id="pomodoro-checkbox"
                      checked={totalPomodoro}
                      onChange={handleChangePomodoro}
                    />
                  }
                  label="Include Total Pomodoros"
                />
                <Button
                  id="show-report-btn"
                  style={{ marginTop: 20 }}
                  variant="outlined"
                  onClick={handleClick}
                >
                  get report
                </Button>
              </FormControl>
            </FormGroup>
          )}
          {reportReady && (
            <Result
              data={response}
              totalHour={totalHour}
              totalPomodoro={totalPomodoro}
              setReportReady={setReportReady}
              reset={reset}
            />
          )}
        </Grid>
      </Grid>
    </div>
  );
}
