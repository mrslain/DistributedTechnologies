namespace java myservice

exception SeviceException {
	1: string error_msg
}

service MyService {
  string get_htmlPage(1:string httpUrl) throws (1: SeviceException e)
}