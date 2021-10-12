# Convert NFA to DFA

## What is this?
This is a Automaton Conversion for 'Fundamentos Teóricos da Computação' for PUC Minas. Here, we need to have a NFA 'translated' in .jff file so the code can understand.

We need to do the best implementation for the conversion and return a DFA.

## How the files will be organized
The following part of code is an example of how jFlap makes a FA file available. 

The parts like `<!--`, `<x>`, `<y>` and `&#13;` can be ignored once it doesn't make a diference.

```jff
<?xml version="1.0" encoding="UTF-8" standalone="no"?><!--Created with JFLAP 7.1.--><structure>&#13;
	<type>fa</type>&#13;
	<automaton>&#13;
		<!--The list of states.-->&#13;
		<state id="0" name="q0">&#13;
			<x>59.0</x>&#13;
			<y>129.0</y>&#13;
			<initial/>&#13;
		</state>&#13;
		<state id="1" name="q1">&#13;
			<x>162.0</x>&#13;
			<y>131.0</y>&#13;
		</state>&#13;
		<state id="2" name="q2">&#13;
			<x>267.0</x>&#13;
			<y>128.0</y>&#13;
		</state>&#13;
		<state id="3" name="q3">&#13;
			<x>376.0</x>&#13;
			<y>128.0</y>&#13;
			<final/>&#13;
		</state>&#13;
		<!--The list of transitions.-->&#13;
		<transition>&#13;
			<from>2</from>&#13;
			<to>3</to>&#13;
			<read>0,1</read>&#13;
		</transition>&#13;
		<transition>&#13;
			<from>0</from>&#13;
			<to>0</to>&#13;
			<read>0,1</read>&#13;
		</transition>&#13;
		<transition>&#13;
			<from>0</from>&#13;
			<to>1</to>&#13;
			<read>1</read>&#13;
		</transition>&#13;
		<transition>&#13;
			<from>1</from>&#13;
			<to>2</to>&#13;
			<read>0,1</read>&#13;
		</transition>&#13;
	</automaton>&#13;
</structure>
```

# Important infos
You need to put the input file in the rcv-folder folder, as well as a txt with the sentences you want to test, separated by an enter (\n).

The result of the convertion will be at the files_out folder.