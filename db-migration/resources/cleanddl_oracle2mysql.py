import re
import sys

ps = [
    (re.compile("-.+\n"), ''),
    (re.compile("\""), ''),
    (re.compile("VARCHAR2"), 'VARCHAR'),
    (re.compile("NUMBER\(.+\)"), 'INT'),
    (re.compile("NUMBER"), 'INT'),
    (re.compile("FLOAT\(.+\)"), 'FLOAT'),
    (re.compile(" ENABLE;"), ';'),
]

def main():
    args = sys.argv[1:]
    filename = args[0]
    with open(filename) as f:
        filecontent = f.read()
        for r, s in ps:
            filecontent = re.sub(r, s, filecontent)
        with open(filename.replace('oracle', 'mysql'), 'w') as w:
            w.write(filecontent)

main()