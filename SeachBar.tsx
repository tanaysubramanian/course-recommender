// import '../styles/main.css';
import { Dispatch, SetStateAction } from 'react';

// Remember that parameter names don't necessarily need to overlap;
// I could use different variable names in the actual function.
interface SearchBarProps {
    value: string, 
    // This type comes from React+TypeScript. VSCode can suggest these.
    //   Concretely, this means "a function that sets a state containing a string"
    setValue: Dispatch<SetStateAction<string>>,
    ariaLabel: string 
  }
  
  // Input boxes contain state. We want to make sure React is managing that state,
  //   so we have a special component that wraps the input box.
  export function SearchBar({value, setValue, ariaLabel}: SearchBarProps) {
    return (
      <input type="text" className="repl-command-box"
            value={value} 
            placeholder="Enter command here!"
            onChange={(ev) => setValue(ev.target.value)}
            aria-label={ariaLabel}>
      </input>
    );
  }