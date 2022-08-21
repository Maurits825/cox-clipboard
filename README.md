# Raids Clipboard
Copies raids info such as kc, points & deaths to the clipboard, now works in CoX and ToB!

# Available Variables
Chambers of Xeric:
| Variable | Description |
| --- | --- |
| $t_pts | total points  
| $p_pts | personal points  
| $kc |kill count  
| $size | team size

<br/>

Theatre of Blood:
| Variable | Description |
| --- | --- |
| $deaths | total deaths  
| $kc | kill count  
| $size | team size  
| $reward | reward value

<br/>

Tombs of Amascut:
| Variable | Description |
| --- | --- |
| $kc | kill count  
| $deaths | total deaths
| $raid_lvl | raid level 
| $invo_count | invocation count
| $size | team size

Any other characters or words can be included in the format, these will stay.

# Examples
format: `kc: $kc total points: $t_pts`  
clipboard text: `kc: 100 total points 63500`

format: `$kc, $deaths, $size, $reward`  
clipboard text: `50, 1, 3, 450600`
