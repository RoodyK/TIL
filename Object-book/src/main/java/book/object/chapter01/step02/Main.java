package book.object.chapter01.step02;

public class Main {

    public static void main(String[] args) {
        Bag bag = new Bag(10000L, new Invitation());
        Audience audience = new Audience(bag);

        Ticket ticket1 = new Ticket();
        Ticket ticket2 = new Ticket();
        TicketOffice ticketOffice = new TicketOffice(5000L, ticket1, ticket2);
        TicketSeller ticketSeller = new TicketSeller(ticketOffice);

        Theater theater = new Theater(ticketSeller);
        theater.enter(audience);

        System.out.println(audience.getBag().hasTicket());
        System.out.println(audience.getBag().getAmount());
    }
}
