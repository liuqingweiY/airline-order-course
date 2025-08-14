import { HttpInterceptorFn } from "@angular/common/http";
import { inject } from "@angular/core";
import { AuthService } from "../services/auth.service";

export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const token = authService.getToken();
  if(token){
    let headers = req.headers;
    headers = headers.set('Authorization', `Bearer ${token}`);
    if (!headers.has('Content-Type') && !(req.body instanceof FormData)) {
      headers = headers.set('Content-Type', 'application/json');
    }

    // const clonedReq = req.clone({
    //   headers: req.headers.set('Authorization', `Bearer ${token}`)
    // });
    // console.log('clonedReq...' + clonedReq.headers.get('Authorization'));
    // console.log('req...' + JSON.stringify(clonedReq));
    const authReq = req.clone({ headers });
    return next(authReq);
  }

  return next(req);
}