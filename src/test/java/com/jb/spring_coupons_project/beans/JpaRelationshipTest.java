
package com.jb.spring_coupons_project.beans;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.CascadeType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JpaRelationshipTest {

    @Test
    void customerOwnsTheJoinTable() throws Exception {
        Field field = Customer.class.getDeclaredField("coupons");

        ManyToMany relationship =
                field.getAnnotation(ManyToMany.class);

        JoinTable joinTable =
                field.getAnnotation(JoinTable.class);

        assertNotNull(relationship);
        assertNotNull(joinTable);

        assertEquals("", relationship.mappedBy());
        assertEquals("customers_vs_coupons", joinTable.name());
        assertEquals("customer_id", joinTable.joinColumns()[0].name());
        assertEquals(
                "coupon_id",
                joinTable.inverseJoinColumns()[0].name()
        );
    }

    @Test
    void couponUsesTheInverseRelationship() throws Exception {
        Field field = Coupon.class.getDeclaredField("customers");

        ManyToMany relationship =
                field.getAnnotation(ManyToMany.class);

        assertNotNull(relationship);
        assertEquals("coupons", relationship.mappedBy());
        assertNull(field.getAnnotation(JoinTable.class));
    }

    @Test
    void sharedEntitiesCannotBeCascadeDeleted() throws Exception {
        ManyToMany customerRelationship =
                Customer.class.getDeclaredField("coupons")
                        .getAnnotation(ManyToMany.class);

        ManyToMany couponRelationship =
                Coupon.class.getDeclaredField("customers")
                        .getAnnotation(ManyToMany.class);

        assertNotNull(customerRelationship);
        assertNotNull(couponRelationship);

        assertArrayEquals(
                new CascadeType[0],
                customerRelationship.cascade()
        );

        assertArrayEquals(
                new CascadeType[0],
                couponRelationship.cascade()
        );
    }

    @Test
    void companyCouponsUseTheExistingForeignKey() throws Exception {
        Field field = Company.class.getDeclaredField("coupons");

        OneToMany relationship =
                field.getAnnotation(OneToMany.class);

        JoinColumn joinColumn =
                field.getAnnotation(JoinColumn.class);

        assertNotNull(relationship);
        assertNotNull(joinColumn);

        assertEquals("company_id", joinColumn.name());
        assertEquals("id", joinColumn.referencedColumnName());
        assertFalse(joinColumn.insertable());
        assertFalse(joinColumn.updatable());
        assertNull(field.getAnnotation(JoinTable.class));
    }

    @Test
    void customerJsonContainsCouponsWithoutRecursion()
            throws Exception {

        Customer customer = new Customer();
        customer.setId(101);
        customer.setEmail("test@example.com");

        Coupon coupon = new Coupon();
        coupon.setId(50);
        coupon.setTitle("Test coupon");

        customer.setCoupons(Set.of(coupon));
        coupon.setCustomers(Set.of(customer));

        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(
                mapper.writeValueAsString(customer)
        );

        assertTrue(json.has("coupons"));
        assertEquals(
                50,
                json.get("coupons").get(0).get("id").asInt()
        );

        assertFalse(
                json.get("coupons").get(0).has("customers")
        );

        assertNotNull(
                Coupon.class.getDeclaredField("customers")
                        .getAnnotation(JsonIgnore.class)
        );
    }
}
