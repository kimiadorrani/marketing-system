import java.util.Scanner ;
import java.util.ArrayList ;
import java.util.HashMap ;
import java.util.Map ;

class Good {
    private String name ;
    private int ID , price ; 
    
    public Good(String name, int ID, int price) {
        this.name = name ; 
        this.ID = ID ; 
        this.price = price ; 
    }
    
    public int getID() {
        return this.ID ; 
    }
    
    public int getPrice() {
        return this.price ; 
    }
    
    public String getName() {
        return this.name ; 
    }
    
    public void setName(String name) {
        this.name = name ;
    }

    public void setID(int ID) {
        this.ID = ID ;
    }

    public void setPrice(int price) {
        this.price = price ;
    }
}

class Discount {
    private Order order = null ;
    private int ID ;
    private int percentage ; 
    
    public Discount () {
        this.percentage = 0 ;
    }
    
    public Discount(int ID, int percent) {
        this.ID = ID ; 
        this.percentage = percent ;
    }
    
    public Order getOrder() {
        return order ;
    }
    
    public void setOrder (Order order) {
        this.order = order ;
    }
    
    public int getID() {
        return ID ;
    }
    
    public void setID(int ID) {
        this.ID = ID ;
    }
    
    public int getPercentage() {
        return this.percentage ; 
    }
    
    public void setPercentage(int percentage) {
        this.percentage = percentage ;
    }
    
}

class Order {
    private HashMap<Good, Integer> items ; 
    private Customer customer ; 
    private Discount discount ;
    private String status ; 
    private int ID ;
    
    public Order(int ID, Customer c) {
        this.discount = new Discount() ;
        this.items = new HashMap<>() ;
        this.status = "pending" ; 
        this.ID = ID ; 
        this.customer = c ; 
    }

    public int getID() {
        return ID;
    }

    public Customer getCustomer() {
        return customer;
    }

    public String getStatus() {
        return status;
    }

    public Discount getDiscount() {
        return discount ;
    }

    public void setItems(HashMap<Good, Integer> items) {
        this.items = items ;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer ;
    }

    public void setID(int ID) {
        this.ID = ID ;
    }

    public void setStatus(String status) {
        this.status = status ;
    }
    
    public void addItem (Good good, int amount) {
        if (this.status.equals("submitted")) return ; 
        items.put(good, amount) ; 
    }
    
    public void removeItem (Good good) {
        if (this.status.equals("submitted")) return ; 
        items.remove(good) ;
    }
    
    public HashMap<Good, Integer> getItems() {
        return items ;  
    }
    
    public int calculatePrice() {
        double sum = 0 ;
        for (Map.Entry mp : items.entrySet()) {
            Good good = (Good) mp.getKey() ; 
            int amount = (int) mp.getValue() ;
            sum = sum + (good.getPrice() * amount) ;  
        }
        double percent = 1.0 - ((double) discount.getPercentage() / 100.0) ;
        sum = percent * sum ;
        return (int) sum ; 
    }
    
    public void addDiscount(Discount discount) {
        this.discount = discount ;
    }

}

class Customer {
    private ArrayList <Order> orders ; 
    private String name ; 
    private int ID ;
    private int balance = 0 ; 
    
    public Customer(String name, int ID) {
        this.orders = new ArrayList<>() ; 
        this.name = name ; 
        this.ID = ID ;
    }

    public String getName() {
        return name;
    }

    public int getID() {
        return ID;
    }

    public int getBalance() {
        return balance;
    }

    public ArrayList<Order> getOrders() {
        return orders;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setBalance(int amount) {
        this.balance = amount ;
    }

    public void setOrders(ArrayList<Order> orders) {
        this.orders = orders;
    }
    
    public void addOrder(Order order) {
        orders.add(order) ; 
    }
    
    public Order[] getTotalOrders() {
        Order myOrders[] = new Order[orders.size()] ;
        for (int i = 0; i < orders.size(); i++) {
            myOrders[i] = orders.get(i) ; 
        }
        return myOrders ;
    }
    
    public Order[] getPendingOrders() {
        ArrayList <Order> tmp = new ArrayList<>() ; 
        for (Order it : orders) {
            if (it.getStatus().equals("pending")) tmp.add(it) ;
        }
        Order myOrders[] = new Order[tmp.size()] ;
        for (int i = 0; i < tmp.size(); i++) {
            myOrders[i] = tmp.get(i) ; 
        }
        return myOrders ;
    }
    
    public Order[] getSubmittedOrders() {
        ArrayList <Order> tmp = new ArrayList<>() ; 
        for (Order it : orders) {
            if (it.getStatus().equals("submitted")) tmp.add(it) ;
        }
        Order myOrders[] = new Order[tmp.size()] ;
        for (int i = 0; i < tmp.size(); i++) {
            myOrders[i] = tmp.get(i) ; 
        }
        return myOrders ;
    }
    
    public void submitOder(Order order) {
        if (order.calculatePrice() > this.balance) return ;
        HashMap<Good, Integer> myItems = order.getItems() ; 
        for (Map.Entry mp : myItems.entrySet()) {
            int minID = Integer.MAX_VALUE , check = -1 ;
            Good good = (Good) mp.getKey() ; 
            int amount = (int) mp.getValue() ;
            for (int i=0 ; i<Market.shop.getRepositoriesArr().size() ; ++i) {
                Repository rep = Market.shop.getRepositoriesArr().get(i) ;
                if (rep.getGoods().containsKey(good) && rep.getGoods().get(good) >= amount) {
                    if (rep.getId() < minID) {
                        minID = rep.getId() ;
                        check = i ; 
                    }
                }
            }
            if (check == -1) {
                return ;
            }
            Market.shop.getRepositoriesArr().get(check).removeGood(good, amount) ;
        }
        order.setStatus("submitted") ;
        this.balance = balance - order.calculatePrice() ;
    }

    @Override
    public String toString() {
        return ID + "," + name + "," + balance + "," + this.getTotalOrders().length + "," +
                this.getSubmittedOrders().length ;
    }
    
}


class Repository {
    private int id , capacity , freeCapacity ; 
    private HashMap<Good,Integer> goods ; 
    
    public Repository(int id, int capacity) {
        this.id = id ;
        this.capacity = capacity ; 
        this.freeCapacity = capacity ;
        this.goods = new HashMap<>() ;
    }
    
    public int getFreeCapacity() {
        return this.freeCapacity ; 
    }
    
    public HashMap<Good,Integer> getGoods() {
        return goods ; 
    }

    public void setFreeCapacity(int freeCapacity) {
        this.freeCapacity = freeCapacity;
    }

    public void setGoods(HashMap<Good, Integer> goods) {
        this.goods = goods;
    }

    public int getCapacity() {
        return capacity ;
    }
    
    public void setCapacity(int capacity) {
        this.capacity = capacity ;
    }
    
    public int getId() {
        return id ;
    }
    
    public void setId(int id) {
        this.id = id ;
    }
    
    public void addGood(Good g, int amount) {
        int newAmount = amount ;
        if (goods.containsKey(g)) {
            newAmount = newAmount + goods.get(g) ;
            goods.replace(g , newAmount) ; 
        } 
        else {
            goods.put(g , newAmount) ;
        }
        this.freeCapacity = freeCapacity - amount ;
    }
    
    public void removeGood(Good g, int amount) {
        int newAmount = goods.get(g) - amount ;
        this.freeCapacity = freeCapacity + amount ;
        goods.replace(g , newAmount) ;
    }

    @Override
    public String toString() {
        return id + "," + capacity + "," + freeCapacity ;
    }
    
}

class Shop {
    private ArrayList <Good> goods ;
    private ArrayList <Customer> customers ; 
    private ArrayList <Repository> repositories ;
    private ArrayList <Discount> discounts ; 
    private HashMap<Good, Integer> itemsSold ;
    private String name ; 
    private int income=0 ;
    
    public Shop(String name) { 
        this.customers = new ArrayList <>() ;
        this.discounts = new ArrayList <>() ;
        this.goods = new ArrayList <>() ;
        this.itemsSold = new HashMap<>() ; 
        this.repositories = new ArrayList <>() ;
        this.name = name ;
    }
    
    public ArrayList<Good> getGoodsArr() {
        return goods;
    }

    public ArrayList<Customer> getCustomersArr() {
        return customers;
    }

    public ArrayList<Repository> getRepositoriesArr() {
        return repositories;
    }

    public ArrayList<Discount> getDiscountsArr() {
        return discounts;
    }

    public String getName() {
        return name;
    }

    public void setGoods(ArrayList<Good> goods) {
        this.goods = goods;
    }

    public void setCustomers(ArrayList<Customer> customers) {
        this.customers = customers;
    }

    public void setRepositories(ArrayList<Repository> repositories) {
        this.repositories = repositories;
    }

    public void setDiscounts(ArrayList<Discount> discounts) {
        this.discounts = discounts;
    }

    public void setItemsSold(HashMap<Good, Integer> itemsSold) {
        this.itemsSold = itemsSold;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public void addCustomer(Customer c) {
        this.customers.add(c) ;
    }
    
    public Customer[] getCustomers() {
        Customer myCustomers[] = new Customer[customers.size()] ;
        for (int i = 0; i < customers.size(); i++) {
            myCustomers[i] = customers.get(i) ; 
        }
        return myCustomers ;
    }
    
    public void addRepository(Repository r) {
        this.repositories.add(r) ;
    }
    
    public Repository[] getRepositories() {
        Repository myRepositories[] = new Repository[repositories.size()] ;
        for (int i = 0; i < repositories.size(); i++) {
            myRepositories[i] = repositories.get(i) ; 
        }
        return myRepositories ;
    }
    
    public int getIncome() {
        return this.income ; 
    }
    
    public void setIncome(int income) {
        this.income = income ; 
    }
    
    public void addGood(Good g) {
        this.goods.add(g) ; 
    }
    
    public Good[] getGoods() {
        Good myGoods[] = new Good[goods.size()] ;
        for (int i = 0; i < goods.size(); i++) {
            myGoods[i] = goods.get(i) ; 
        }
        return myGoods ;
    }
    
    public void addDiscount(Discount discount) {
        discounts.add(discount) ;
    }
    
    public void addDiscount(Discount d, Order o) {
        o.addDiscount(d) ;
    }
    
    public HashMap<Good, Integer> getItemsSold() {
        return itemsSold ; 
    }
    
    public void increamentGood(Good g, int amount) {
        Repository rep[] = (Repository[]) repositories.toArray() ;
        int size = repositories.size() ;
        // sort repositories by capacity
        for (int i = size - 1; i >= 0; i--) {
            int k=i ; 
            int maxCap = rep[i].getCapacity() ;
            for (int j = i - 1; j >= 0; j--) {
                if (maxCap < rep[j].getCapacity()) {
                    maxCap = rep[j].getCapacity() ;
                    k = j ; 
                }
            }
            Repository tmpRep = rep[i] ; 
            rep[i] = rep[k] ;
            rep[k] = tmpRep ; 
        }
        // add good g to a repository
        for (int i = 0; i < size; i++) {
            if (amount < rep[i].getFreeCapacity()) {
                rep[i].addGood(g , amount) ;
                break ; 
            }
        }
    }
    
}



public class Market {
    
    public static Shop shop = new Shop("myShop") ;
    public static ArrayList <Order> orders = new ArrayList<>() ;
    
    public static void main(String[] args) {
        Scanner in = new Scanner (System.in) ; 
        while (true) {
            String s = in.next() ; 
            if (s.equals("add")) {
                String tmp = in.next() ; 
                if (tmp.equals("customer")) {
                    int id = in.nextInt() ; 
                    String name = in.next() ;
                    Customer c = new Customer(name, id) ;
                    shop.getCustomersArr().add(c) ;
                }
                else if (tmp.equals("good")) {
                    int id, price ;
                    id = in.nextInt() ; 
                    String name = in.next() ;
                    price = in.nextInt() ;
                    Good g = new Good(name, id, price) ;
                    shop.getGoodsArr().add(g) ; 
                    int number = in.nextInt() ; 
                    for (Repository it : shop.getRepositoriesArr()) {
                        if (it.getFreeCapacity() > number) {
                            it.addGood(g, number);
                            break ;
                        }
                    }
                }
                else if (tmp.equals("repository")) {
                    int id = in.nextInt() ; 
                    int capacity = in.nextInt() ; 
                    Repository r = new Repository(id, capacity) ;
                    shop.getRepositoriesArr().add(r) ;
                }
                else if (tmp.equals("order")) {
                    int orderID = in.nextInt() ; 
                    int customerID = in.nextInt() ; 
                    Customer c = null ;
                    for (Customer it : shop.getCustomersArr()) {
                        if (it.getID() == customerID) {
                            c = it ; 
                            break ;
                        }
                    }
                    Order o = new Order(orderID, c) ;
                    c.addOrder(o) ;
                    orders.add(o) ; 
                }
                else if (tmp.equals("balance")) {
                    int ID = in.nextInt() ; 
                    int amount = in.nextInt() ;
                    Customer c = null ;
                    for (Customer it : shop.getCustomersArr()) {
                        if (it.getID() == ID) {
                            c = it ; 
                            break ;
                        }
                    }
                    int newBalance = c.getBalance() + amount ; 
                    c.setBalance(newBalance) ;
                }
                else if (tmp.equals("item")) {
                    int orderID = in.nextInt() ;
                    int goodID = in.nextInt() ; 
                    int amount = in.nextInt() ;
                    Good g = null ; 
                    for (int i=0; i < shop.getGoodsArr().size(); i++) {
                        if (shop.getGoodsArr().get(i).getID() == goodID) {
                            g = shop.getGoodsArr().get(i) ; 
                            break ; 
                        } 
                    }
                    for (int i=0; i < orders.size(); i++) {
                        if (orders.get(i).getID() == orderID) {
                            orders.get(i).addItem(g, amount);
                            break ; 
                        }
                    }
                }
                else if (tmp.equals("discount")) {
                    int id = in.nextInt() ; 
                    int percent = in.nextInt() ; 
                    Discount d = new Discount (id, percent) ;
                    shop.getDiscountsArr().add(d) ; 
                }
            }
            else if (s.equals("report")) {
                String tmp = in.next() ; 
                if (tmp.equals("customers")) {
                    for (Customer it : shop.getCustomersArr()) 
                        System.out.println(it.toString()) ;
                }
                else if (tmp.equals("repositories")) {
                    for (Repository it : shop.getRepositoriesArr()) 
                        System.out.println(it.toString()) ;
                }
                else if (tmp.equals("income")) {
                    System.out.println(shop.getIncome()) ;
                }
            }
            else if (s.equals("remove")) {
                String tmp = in.next() ;
                if (tmp.equals("item")) {
                    int orderID = in.nextInt() ; 
                    int goodID = in.nextInt() ;
                    Good g=null ; 
                    for (int i=0; i < shop.getGoodsArr().size(); i++) {
                        if (shop.getGoodsArr().get(i).getID() == goodID) {
                            g = shop.getGoodsArr().get(i) ; 
                            break ; 
                        } 
                    }
                    Order o=null ; 
                    for (Order it : orders) {
                        if (it.getID() == orderID) {
                            o = it ; 
                            break ; 
                        }
                    }
                    o.removeItem(g) ;
                }
            }
            else if (s.equals("submit")) {
                String tmp = in.next() ;
                if (tmp.equals("order")) {
                    int orderID = in.nextInt() ; 
                    Order o=null ; 
                    for (Order it : orders) {
                        if (it.getID() == orderID) {
                            o = it ; 
                            break ; 
                        }
                    }
                    Customer c = o.getCustomer() ; 
                    c.submitOder(o) ;
                    int newIncome = shop.getIncome() + o.calculatePrice() ;
                    shop.setIncome(newIncome) ;
                }
                else if (tmp.equals("discount")) {
                    int orderID = in.nextInt() ;
                    int discountID = in.nextInt() ;
                    Order o=null ; 
                    for (Order it : orders) {
                        if (it.getID() == orderID) {
                            o = it ; 
                            break ; 
                        }
                    }
                    Discount d=null ; 
                    for (Discount it : shop.getDiscountsArr()) { 
                        if (it.getID() == discountID) {
                            d = it ; 
                            break ;
                        }
                    }
                    if (d!=null) {
                        o.addDiscount(d) ;
                        shop.getDiscountsArr().remove(d) ;
                    }
                }
            }
            else if (s.equals("terminate")) System.exit(0) ;
        }
    }
    
}