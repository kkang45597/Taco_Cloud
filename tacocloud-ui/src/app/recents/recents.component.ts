// import { Component, OnInit, Injectable } from '@angular/core';
// import { HttpClient } from '@angular/common/http';

// @Component({
//   selector: 'recent-tacos',
//   templateUrl: 'recents.component.html',
//   styleUrls: ['./recents.component.css']
// })

// @Injectable()
// export class RecentTacosComponent implements OnInit {
//   recentTacos: any;

//   constructor(private httpClient: HttpClient) { }

//   ngOnInit() {
//     this.httpClient.get('http://localhost:8080/design/recent') // <1>
//         .subscribe(data => this.recentTacos = data);
//   }
  
// }

import { Component, OnInit, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CartService } from '../cart/cart-service'; // Ensure the correct import path for CartService
import { Router } from '@angular/router'; // Import Router

@Component({
  selector: 'recent-tacos',
  templateUrl: 'recents.component.html',
  styleUrls: ['./recents.component.css']
})

@Injectable()
export class RecentTacosComponent implements OnInit {
  recentTacos: any;

  // Inject CartService and Router in the constructor and mark them as private
  constructor(private httpClient: HttpClient, private cartService: CartService, private router: Router) { }

  ngOnInit() {
    this.httpClient.get('http://localhost:8080/design/recent')
        .subscribe(data => this.recentTacos = data);
  }

  orderTaco(taco: any) {
    this.cartService.addToCart(taco); // Add taco to cart
    this.router.navigate(['/cart']); // Navigate to the cart page
  }
}


