"""Shows the state of a Central Portal deployment and its validation errors.

Usage: python central_status.py <deploymentId>

Reads the portal token from ~/.m2/settings.xml and never prints it. The server
id is the release profile's <publishingServerId> in the project's pom.xml
(central-fvarrui for fxorms), or "central" if there is none. States: PENDING, VALIDATING, VALIDATED (waiting for manual
publishing), PUBLISHING, PUBLISHED, FAILED.
"""
import base64
import json
import os
import re
import sys
import urllib.request
import xml.etree.ElementTree as ET


def server_id():
    pom = os.path.join(os.path.dirname(os.path.abspath(__file__)), '..', '..', '..', 'pom.xml')
    if os.path.exists(pom):
        match = re.search(r'<publishingServerId>([^<]+)</publishingServerId>', open(pom, encoding='utf-8').read())
        if match:
            return match.group(1)
    return 'central'


def central_token(server):
    settings = os.path.join(os.path.expanduser('~'), '.m2', 'settings.xml')
    root = ET.parse(settings).getroot()
    ns = {'m': root.tag.split('}')[0].strip('{')} if root.tag.startswith('{') else {'m': ''}
    prefix = 'm:' if ns['m'] else ''
    for entry in root.iterfind(f'.//{prefix}servers/{prefix}server', ns):
        if entry.findtext(f'{prefix}id', namespaces=ns) == server:
            user = entry.findtext(f'{prefix}username', namespaces=ns)
            password = entry.findtext(f'{prefix}password', namespaces=ns)
            return base64.b64encode(f'{user}:{password}'.encode()).decode()
    sys.exit(f'No server "{server}" in {settings}')


def main():
    if len(sys.argv) != 2:
        sys.exit(__doc__)
    url = 'https://central.sonatype.com/api/v1/publisher/status?id=' + sys.argv[1]
    request = urllib.request.Request(url, method='POST', headers={'Authorization': 'Bearer ' + central_token(server_id())})
    status = json.load(urllib.request.urlopen(request))
    print('Deployment:', status.get('deploymentId'), '-', status.get('deploymentState'))
    for component, errors in (status.get('errors') or {}).items():
        for error in errors:
            print(f'  {component}: {error}')
    for purl in status.get('purls') or []:
        print('  ' + purl)


if __name__ == '__main__':
    main()
