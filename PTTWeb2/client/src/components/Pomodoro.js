import { useEffect, useState } from "react";
import {
  FormControl,
  Select,
  MenuItem,
  InputLabel,
  Grid,
  OutlinedInput,
  Typography,
  Button,
} from "@mui/material";
import Timer from "./Timer";

export default function Pomodoro({ id, projects }) {
  const [selectedProjectId, setSelectedProjectId] = useState(-1);
  const [countdown, setCountdown] = useState(10);
  const [isRest, setIsRest] = useState(false);
  const [isTimer, setIsTimer] = useState(false);
  const [counter, setCounter] = useState(0);
  const [sessionId, setSessionId] = useState(0);
  const [stopDialog, setStopDialog] = useState(false);
  const [continueDialog, setContinueDialog] = useState(false);

  const projList = [{ id: -1, projectname: "No Project" }];
  Array.prototype.push.apply(projList, projects);

  const gridStyle = {
    padding: 20,
    height: "60vh",
    width: 500,
    margin: "20px auto",
  };

  const handleStart = () => {
    setIsRest(false);
    setIsTimer(true);
    setCountdown(5);
  };

  const handleStop = () => {
    setStopDialog(true);
    setIsTimer(false);
    setIsRest(false);
  };

  const handleSelect = (event) => {
    const {
      target: { value },
    } = event;
    console.log(value);
    setSelectedProjectId(value);
  };
  // console.log(selectedProject);

  return (
    <div style={{ margin: 30 }}>
      <Typography variant="inherit" component="h1" style={{ padding: 0 }}>
        Pomodoro
      </Typography>
      <Grid style={gridStyle}>
        <Grid
          container
          style={{ justifyContent: "center", alignContent: "center" }}
        >
          {!isTimer && (
            <FormControl sx={{ width: 200 }}>
              <InputLabel>Project</InputLabel>
              <Select
                id="projList"
                value={selectedProjectId}
                onChange={handleSelect}
                input={<OutlinedInput label="Project" />}
              >
                {projList.map((project) => (
                  <MenuItem key={project.id} value={project.id}>
                    {project.projectname}
                  </MenuItem>
                ))}
              </Select>
              <Button
                style={{ marginTop: 20 }}
                variant="outlined"
                onClick={handleStart}
              >
                start
              </Button>
            </FormControl>
          )}
          {isTimer && (
            <div>
              <Timer
                countdown={countdown}
                setCountdown={setCountdown}
                isRest={isRest}
                setIsRest={setIsRest}
                setIsTimer={setIsTimer}
                counter={counter}
                setCounter={setCounter}
                sessionId={sessionId}
                setSessionId={setSessionId}
                setContinueDialog={setContinueDialog}
              />
              <Button variant="outlined" onClick={handleStop}>
                stop
              </Button>
            </div>
          )}
        </Grid>
        <Grid
          container
          item
          style={{
            // marginTop: 20,
            padding: 0,
            height: 60,
            alignItems: "center",
            justifyContent: "space-evenly",
          }}
        >
          {/* {!isTimer && (
          )}
          {isTimer && (
          )} */}
        </Grid>
      </Grid>
      {continueDialog && <></>}
      {stopDialog && <></>}
    </div>
  );
}
