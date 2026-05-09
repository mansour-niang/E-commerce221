package dakar_tech_wallet;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

/**
 * Entity riche représentant un compte utilisateur.
 * - Identité : UUID
 * - Solde : Money (immutable Value Object)
 * Toutes les opérations métier passent par des méthodes explicites : crediter, debiter, transfererVers.
 */
public class Account {

    private final UUID id;
    private final PhoneNumber owner;
    private Money balance;

    public Account(UUID id, PhoneNumber owner, Money initialBalance) {
        this.id = id == null ? UUID.randomUUID() : id;
        this.owner = Objects.requireNonNull(owner, "owner is required");
        this.balance = Objects.requireNonNull(initialBalance, "initial balance is required");
    }

    public UUID getId() { return id; }

    public PhoneNumber getOwner() { return owner; }

    /**
     * Retourne une copie immuable du solde courant. Money est un Value Object immuable.
     */
    public Money getBalance() { return balance; }

    public synchronized void crediter(Money montant) {
        Objects.requireNonNull(montant, "montant is required");
        if (!montant.currency().equalsIgnoreCase(balance.currency())) {
            throw new IllegalArgumentException("Devise différente lors du credit");
        }
        this.balance = this.balance.add(montant);
    }

    public synchronized void debiter(Money montant) {
        Objects.requireNonNull(montant, "montant is required");
        if (!montant.currency().equalsIgnoreCase(balance.currency())) {
            throw new IllegalArgumentException("Devise différente lors du debit");
        }
        if (this.balance.amount().compareTo(montant.amount()) < 0) {
            throw new IllegalArgumentException("Solde insuffisant pour le débit");
        }
        BigDecimal newAmount = this.balance.amount().subtract(montant.amount());
        // newAmount >= 0 by previous check
        this.balance = new Money(newAmount, this.balance.currency());
    }

    /**
     * Transfert sécurisé et atomique entre deux comptes.
     * Les vérifications de devise et de suffisance sont effectuées avant modification.
     */
    public void transfererVers(Account destinataire, Money montant) {
        Objects.requireNonNull(destinataire, "destinataire is required");
        Objects.requireNonNull(montant, "montant is required");

        // check currencies match for both accounts and montant
        if (!this.balance.currency().equalsIgnoreCase(montant.currency())) {
            throw new IllegalArgumentException("Devise différente entre montant et compte source");
        }
        if (!destinataire.balance.currency().equalsIgnoreCase(montant.currency())) {
            throw new IllegalArgumentException("Devise différente entre comptes");
        }

        // acquire locks in consistent order to avoid deadlocks
        Account first = this.id.compareTo(destinataire.id) < 0 ? this : destinataire;
        Account second = first == this ? destinataire : this;

        synchronized (first) {
            synchronized (second) {
                if (this.balance.amount().compareTo(montant.amount()) < 0) {
                    throw new IllegalArgumentException("Solde insuffisant pour le transfert");
                }

                // effectue le transfert
                this.balance = new Money(this.balance.amount().subtract(montant.amount()), this.balance.currency());
                destinataire.balance = destinataire.balance.add(montant);
            }
        }
    }

    @Override
    public String toString() {
        return "Account{" + id + ", owner=" + owner + ", balance=" + balance + '}';
    }
}

