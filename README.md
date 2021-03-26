# Raids Clipboard
Copies raids info such as kc, points & deaths to the clipboard, now works in CoX and ToB!

# How to use
The available variables for CoX are:
| Variable | Description |
| --- | --- |
| $t_pts | total points  
| $p_pts | personal points  
| $kc |kill count  
| $size | team size

<br/>

The available variables for ToB are:
| Variable | Description |
| --- | --- |
| $deaths | total deaths  
| $kc | kill count  
| $size | team size  
| $reward | reward value

Any other characters or words can be included in the format, these will stay.

# Examples
format: `kc: $kc total points: $t_pts`  
clipboard text: `kc: 100 total points 63500`

format: `$kc, $deaths, $size, $reward`  
clipboard text: `50, 1, 3, 450600`
